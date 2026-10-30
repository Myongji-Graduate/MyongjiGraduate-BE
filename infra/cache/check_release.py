#!/usr/bin/env python3
"""Offline DEV-121 release checks. Never connects to a database or deploys."""
import argparse
import os
from pathlib import Path
import re
import sys


REQUIRED_MIGRATIONS = {
    "1": "V1__add_failure_reason_and_details_to_parsing_text_history.sql",
    "2": "V2__create_optional_mandatory_policy.sql",
    "3": "V3__scope_basic_academical_culture_policy.sql",
    "4": "V4__map_basic_programming_major_transition.sql",
    "5": "V5__create_fusion_major_policy.sql",
    "6": "V6__expand_humanities_fusion_majors.sql",
    "7": "V7__complete_fusion_basic_course_mappings.sql",
    "8": "V8__track_anonymous_parsing_requests.sql",
    "9": "V9__correct_applied_data_science_fusion_basic_credit.sql",
    "10": "V10__add_honors_college_to_user.sql",
    "11": "V11__add_honors_target_major_to_user.sql",
    "14": "V14__add_taken_lecture_revision.sql",
    "15": "V15__version_lecture_catalog.sql",
}


def check_release(directory, mode, namespace):
    errors = []
    if mode not in ("off", "shadow", "on"):
        errors.append("TAKEN_LECTURE_CACHE_MODE must be off, shadow, or on")
    if not re.fullmatch(r"[A-Za-z0-9._-]+", namespace):
        errors.append("TAKEN_LECTURE_CACHE_NAMESPACE must contain only letters, digits, dot, underscore, or hyphen")
    if not directory.is_dir():
        return errors + ["Migration directory does not exist"]
    versions = {}
    for path in sorted(directory.glob("V*.sql")):
        match = re.fullmatch(r"V([0-9]+(?:[._][0-9]+)*)__.+\.sql", path.name)
        if not match:
            errors.append(f"Invalid versioned migration filename: {path.name}")
            continue
        # Flyway treats leading zeroes and trailing zero version components as equivalent.
        version = [int(part) for part in re.split(r"[._]", match[1])]
        while len(version) > 1 and version[-1] == 0:
            version.pop()
        key = tuple(version)
        if key in versions:
            errors.append(f"Duplicate migration version: {versions[key]} / {path.name}")
        versions[key] = path.name
        if version[0] in (12, 13):
            errors.append(f"DEV-121 ships before DEV-116: do not include {path.name}; assign unapplied later migrations a version above the latest deployed version")
    for version, filename in REQUIRED_MIGRATIONS.items():
        if versions.get((int(version),)) != filename:
            errors.append(f"DEV-121 release requires {filename}; preserve V1-V11 history and the V14/V15 cache migrations")
    return errors


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--migration-dir", type=Path,
                        default=Path(__file__).resolve().parents[2] / "src/main/resources/db/migration")
    parser.add_argument("--mode", default=os.environ.get("TAKEN_LECTURE_CACHE_MODE", "off"))
    parser.add_argument("--namespace", default=os.environ.get("TAKEN_LECTURE_CACHE_NAMESPACE", "prod-dev121-v3"))
    args = parser.parse_args()
    errors = check_release(args.migration_dir, args.mode, args.namespace)
    for error in errors:
        print(error, file=sys.stderr)
    if errors:
        return 1
    print("DEV-121 release inputs valid: V1-V11 history -> V14/V15; no V12/V13 dependency; cache settings checked (offline only)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
