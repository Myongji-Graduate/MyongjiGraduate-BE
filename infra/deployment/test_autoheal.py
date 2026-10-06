"""Test remote deployment control flow without a Docker daemon or network."""

import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest


SCRIPT = Path(__file__).with_name("ensure-autoheal.sh")
DOCKER_STUB = r'''
import json, os, pathlib, sys
root = pathlib.Path(os.environ["STUB_ROOT"])
args = sys.argv[1:]
with (root / "calls").open("a") as out:
    out.write(json.dumps(args) + "\n")
mode = os.environ["STUB_MODE"]
if args[:2] == ["container", "inspect"]:
    sys.exit(0 if mode in ("existing", "stopped", "unhealthy") else 1)
if args[:2] == ["image", "inspect"]:
    sys.exit(0 if mode in ("cached", "run_failure") else 1)
if args[0] == "inspect":
    if "Running" in args[2]:
        print("false" if mode == "stopped" else "true")
    else:
        print("unhealthy" if mode == "unhealthy" else "healthy")
elif args[0] == "pull":
    counter = root / "pulls"
    count = int(counter.read_text()) + 1 if counter.exists() else 1
    counter.write_text(str(count))
    if mode == "pull_failure" or (mode == "retry" and count < 3):
        sys.exit(124)
elif args[0] == "run" and mode == "run_failure":
    sys.exit(1)
elif args[0] not in ("run", "start"):
    sys.exit("Unexpected Docker command")
'''


class AutohealDeploymentTest(unittest.TestCase):
    def run_deploy(self, mode):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            stubs = {
                "docker": f"#!{sys.executable}\n" + DOCKER_STUB,
                "sleep": "#!/bin/sh\nexit 0\n",
                "timeout": (
                    "#!/bin/sh\n"
                    '[ "$1" = "--kill-after=10s" ] && [ "$2" = "180s" ] || exit 2\n'
                    'shift 2\nexec "$@"\n'
                ),
            }
            for name, body in stubs.items():
                path = root / name
                path.write_text(body)
                path.chmod(0o700)
            result = subprocess.run(
                ["bash", str(SCRIPT)],
                env={**os.environ, "PATH": f"{root}:{os.environ['PATH']}",
                     "STUB_ROOT": str(root), "STUB_MODE": mode},
                capture_output=True, text=True, timeout=15,
            )
            calls = [json.loads(line) for line in (root / "calls").read_text().splitlines()]
        self.assertFalse(any(call[0] == "rm" for call in calls))
        self.assertFalse(any("mju-graduate-server" in call for call in calls))
        return result, calls

    def test_running_autoheal_is_preserved(self):
        result, calls = self.run_deploy("existing")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertFalse(any(call[0] in ("pull", "run", "start") for call in calls))

    def test_stopped_autoheal_is_started_without_recreation(self):
        result, calls = self.run_deploy("stopped")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn(["start", "autoheal"], calls)
        self.assertFalse(any(call[0] in ("pull", "run") for call in calls))

    def test_cached_image_does_not_require_registry_access(self):
        result, calls = self.run_deploy("cached")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertFalse(any(call[0] == "pull" for call in calls))
        self.assertEqual(sum(call[0] == "run" for call in calls), 1)

    def test_fresh_install_pulls_then_starts(self):
        result, calls = self.run_deploy("fresh")
        self.assertEqual(result.returncode, 0, result.stderr)
        commands = [call[0] for call in calls]
        self.assertLess(commands.index("pull"), commands.index("run"))
        self.assertIn("Autoheal is healthy", result.stdout)

    def test_transient_pull_timeouts_are_retried(self):
        result, calls = self.run_deploy("retry")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(sum(call[0] == "pull" for call in calls), 3)
        self.assertEqual(sum(call[0] == "run" for call in calls), 1)

    def test_exhausted_retries_fail_without_starting_container(self):
        result, calls = self.run_deploy("pull_failure")
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(sum(call[0] == "pull" for call in calls), 3)
        self.assertFalse(any(call[0] == "run" for call in calls))
        self.assertIn("application deployment has already completed", result.stderr)

    def test_container_start_failure_is_not_hidden(self):
        result, _ = self.run_deploy("run_failure")
        self.assertNotEqual(result.returncode, 0)
        self.assertNotIn("Autoheal is healthy", result.stdout)

    def test_unhealthy_autoheal_is_reported_as_failure(self):
        result, calls = self.run_deploy("unhealthy")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("did not become healthy", result.stderr)
        self.assertFalse(any(call[0] == "run" for call in calls))


if __name__ == "__main__":
    unittest.main()
