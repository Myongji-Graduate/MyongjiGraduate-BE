from collections import Counter
import copy
import importlib.util
from pathlib import Path
import unittest

spec = importlib.util.spec_from_file_location("main_plan", Path(__file__).with_name("main-plan.py"))
plan = importlib.util.module_from_spec(spec)
spec.loader.exec_module(plan)


class MainScheduleTest(unittest.TestCase):
    def setUp(self):
        self.tasks = [{"id": f"T{i:02}", "wallTimeSeconds": 900 if i <= 3 else 1200,
                       "requiredSuites": ["takenlecture-unit"] + (["takenlecture-integration"] if i > 3 else [])}
                      for i in range(1, 6)]

    def test_schedule_contains_every_task_condition_three_times_with_510_minute_cap(self):
        rows = plan.schedule_rows(self.tasks, 3, "DEV-122-P3-v1")
        self.assertEqual(30, len({r["runId"] for r in rows}))
        self.assertEqual(list(range(1, 31)), [r["orderIndex"] for r in rows])
        self.assertEqual(510 * 60, sum(r["wallTimeSeconds"] for r in rows))
        counts = Counter((r["taskId"], r["condition"]) for r in rows)
        self.assertEqual(10, len(counts))
        self.assertEqual({3}, set(counts.values()))
        self.assertEqual(sorted(r["orderKeySha256"] for r in rows), [r["orderKeySha256"] for r in rows])
        self.assertEqual(rows, plan.schedule_rows(self.tasks, 3, "DEV-122-P3-v1"))

    def test_repetition_or_budget_expansion_is_rejected(self):
        with self.assertRaisesRegex(ValueError, "exactly three"):
            plan.schedule_rows(self.tasks, 4, "seed")
        tasks = copy.deepcopy(self.tasks)
        tasks[-1]["wallTimeSeconds"] = 1500
        with self.assertRaisesRegex(ValueError, "budget"):
            plan.schedule_rows(tasks, 3, "seed")

    def test_missing_duplicate_or_reordered_task_definitions_are_rejected(self):
        for tasks in [self.tasks[:-1], [self.tasks[0], *self.tasks[1:-1], self.tasks[0]], self.tasks[::-1]]:
            with self.subTest(tasks=tasks), self.assertRaisesRegex(ValueError, "five unique"):
                plan.schedule_rows(tasks, 3, "seed")


if __name__ == "__main__":
    unittest.main()
