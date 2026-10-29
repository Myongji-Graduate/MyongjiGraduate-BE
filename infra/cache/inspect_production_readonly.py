#!/usr/bin/env python3
"""Run on the existing production host via SSH stdin; output metadata only.

Uses the running app's credentials in process memory / MYSQL_PWD only. Does not
start the application, run Flyway, alter server settings, or read student rows.
No docker inspect output, credentials, raw grants, or raw errors are printed.
"""
import datetime
import json
import os
import subprocess
import urllib.parse


SAFE_ENV = {
    "SPRING_PROFILES_ACTIVE", "TAKEN_LECTURE_CACHE_MODE", "TAKEN_LECTURE_CACHE_NAMESPACE",
    "SHARED_MASTER_CACHE_ENABLED", "JAVA_OPTS", "SPRING_DATA_REDIS_HOST",
    "SPRING_DATA_REDIS_PORT", "SPRING_FLYWAY_ENABLED", "SPRING_FLYWAY_OUT_OF_ORDER",
    "SPRING_FLYWAY_BASELINE_VERSION",
}
QUERIES = {
    "server": "SELECT JSON_OBJECT('version',VERSION(),'comment',@@version_comment,'transactionIsolation',@@transaction_isolation,'logBin',@@log_bin,'trustFunctionCreators',@@log_bin_trust_function_creators)",
    "history": "SELECT JSON_OBJECT('rank',installed_rank,'version',version,'type',type,'script',script,'checksum',checksum,'success',success) FROM flyway_schema_history ORDER BY installed_rank",
    "engines": "SELECT JSON_OBJECT('table',table_name,'engine',engine) FROM information_schema.tables WHERE table_schema=DATABASE() AND table_name IN ('user','lecture','taken_lecture','lecture_catalog_revision','graduation_policy_snapshot_archive','graduation_policy_release') ORDER BY table_name",
    "revisionColumn": "SELECT JSON_OBJECT('name',column_name,'type',column_type,'nullable',is_nullable,'default',column_default) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='user' AND column_name='taken_lecture_revision'",
    "lectureTriggers": "SELECT JSON_OBJECT('name',trigger_name,'event',event_manipulation,'timing',action_timing) FROM information_schema.triggers WHERE trigger_schema=DATABASE() AND event_object_table='lecture' ORDER BY trigger_name",
}


def run(command, **kwargs):
    return subprocess.run(command, capture_output=True, text=True, check=True, timeout=20, **kwargs).stdout


def main():
    app, redis = json.loads(run(["docker", "inspect", "mju-graduate-server", "mg-redis"]))
    env = dict(x.split("=", 1) for x in app["Config"]["Env"] if "=" in x)
    if env.get("SPRING_PROFILES_ACTIVE") != "prod" or not app["State"]["Running"]:
        raise RuntimeError("Unexpected runtime target")
    url = urllib.parse.urlparse(env["SPRING_DATASOURCE_URL"].removeprefix("jdbc:"))
    if url.scheme != "mysql" or not url.hostname or not url.path.strip("/"):
        raise RuntimeError("Unexpected datasource URL")
    database = url.path.strip("/")
    client_env = os.environ.copy()
    client_env["MYSQL_PWD"] = env["SPRING_DATASOURCE_PASSWORD"]
    command = ["mysql", "--connect-timeout=5", "--batch", "--raw", "--skip-column-names",
               "--host", url.hostname, "--port", str(url.port or 3306),
               "--user", env["SPRING_DATASOURCE_USERNAME"], "--database", database]
    result = {"observedAtUtc": datetime.datetime.now(datetime.timezone.utc).isoformat(),
              "scope": "read-only metadata; no DDL, DML, migration, restart, deploy, or configuration mutation"}
    result["runtime"] = {
        "image": app["Config"]["Image"], "health": app["State"].get("Health", {}).get("Status"),
        "startedAt": app["State"]["StartedAt"], "restartCount": app["RestartCount"],
        "memoryBytes": app["HostConfig"]["Memory"],
        "environment": {k: v for k, v in env.items() if k in SAFE_ENV},
        "redisImage": redis["Config"]["Image"],
    }
    for name, sql in QUERIES.items():
        rows = run(command + ["--execute", "SET SESSION MAX_EXECUTION_TIME=5000; START TRANSACTION READ ONLY; " + sql + "; ROLLBACK;"], env=client_env)
        result[name] = [json.loads(row) for row in rows.splitlines() if row]
    grants = run(command + ["--execute", "SHOW GRANTS"], env=client_env)
    privileges = set()
    # Report only direct privileges covering this database; do not expose account names.
    for line in grants.splitlines():
        if " ON " not in line:
            continue
        prefix, suffix = line.split(" ON ", 1)
        scope = suffix.split(" TO ", 1)[0].split(" FROM ", 1)[0]
        if scope not in ("*.*", "`" + database.replace("`", "``") + "`.*"):
            continue
        if prefix.startswith("GRANT "):
            privileges.update(prefix[6:].split(", "))
        elif prefix.startswith("REVOKE "):
            privileges.difference_update(prefix[7:].split(", "))
    result["migrationPrivileges"] = {p: p in privileges or "ALL PRIVILEGES" in privileges
                                     for p in ("SELECT", "INSERT", "UPDATE", "CREATE", "ALTER", "REFERENCES", "TRIGGER")}
    info = run(["docker", "exec", "mg-redis", "redis-cli", "INFO", "server"])
    result["runtime"]["redisVersion"] = next(x.split(":", 1)[1] for x in info.splitlines() if x.startswith("redis_version:"))
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    try:
        main()
    except Exception as error:
        print(json.dumps({"errorClass": type(error).__name__, "details": "suppressed to avoid credential exposure"}))
        raise SystemExit(1)
