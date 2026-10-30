#!/usr/bin/env python3
"""Summarize complete synthetic HTTP runs without pooling unrelated latency samples."""

import argparse
import hashlib
import json
import statistics
from collections import defaultdict
from pathlib import Path


def compare(report):
    if report.get("complete") is not True:
        raise ValueError("Only a complete HTTP run can be compared")
    groups = defaultdict(list)
    for row in report["measurements"]:
        key = (row["variant"], row["mode"], row["endpoint"], row["size"], row["concurrency"])
        groups[key].append(row)
    variants = sorted({key[0] for key in groups})
    if variants != ["optimized", "previous"]:
        raise ValueError("Both previous and optimized JARs are required")
    modes = sorted({key[1] for key in groups})
    if not {"off", "on"}.issubset(modes):
        raise ValueError("Both OFF and ON are required")
    medians = {}
    for key, rows in groups.items():
        if sorted(row["round"] for row in rows) != [1, 2, 3]:
            raise ValueError(f"Expected exactly three independent rounds: {key}")
        medians[key] = {metric: statistics.median(row[metric] for row in rows)
                        for metric in ("p50Ms", "p95Ms", "p99Ms", "selectsPerRequest")}
    conditions = sorted({key[2:] for key in groups})
    comparisons = []
    for endpoint, size, concurrency in conditions:
        arms = {f"{variant}_{mode}": medians[variant, mode, endpoint, size, concurrency]
                for variant in variants for mode in modes}

        def change(after, before):
            return (arms[after]["p95Ms"] / arms[before]["p95Ms"] - 1) * 100

        comparisons.append({
            "endpoint": endpoint, "size": size, "concurrency": concurrency,
            "medianOfThreeRuns": arms,
            "previousOnVsOffPercent": change("previous_on", "previous_off"),
            "optimizedOnVsOffPercent": change("optimized_on", "optimized_off"),
            "optimizedOnVsPreviousOnPercent": change("optimized_on", "previous_on"),
            "roundP95Ms": {f"{variant}_{mode}": [r["p95Ms"] for r in sorted(
                groups[variant, mode, endpoint, size, concurrency], key=lambda r: r["round"])]
                for variant in variants for mode in modes},
        })
    return {"modes": modes, "statistic": "median of three per-batch p95 values; not pooled-request p95",
            "performanceRequests": sum(row["requests"] for row in report["measurements"]),
            "comparisons": comparisons}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("report", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    raw = args.report.read_bytes()
    result = compare(json.loads(raw))
    result["sourceSha256"] = hashlib.sha256(raw).hexdigest()
    args.output.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n")
    print(f"{len(result['comparisons'])} conditions, {result['performanceRequests']} performance GETs")


if __name__ == "__main__":
    main()
