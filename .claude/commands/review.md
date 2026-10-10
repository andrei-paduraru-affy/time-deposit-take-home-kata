---
description: Run the reviewer subagent on the current diff
argument-hint: "[optional git range or focus area]"
---

Use the `reviewer` subagent to review the current changes.

Scope: $ARGUMENTS (if empty, review `git diff HEAD` plus untracked files).

Relay its findings to me as-is, ordered by severity. Do not apply any fixes until I say which ones to address.
