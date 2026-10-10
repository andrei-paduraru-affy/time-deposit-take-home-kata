# Prompts & Decisions Log

Short log of the key prompts given to the agent and the decisions that came out of them. Newest last.

| # | Date | Prompt (summary) | Outcome / decision |
|---|------|------------------|--------------------|
| 1 | 2026-10-10 | "Remove every other language besides java" | Deleted the C#, Kotlin, Python and TypeScript starter projects; only `java/` remains. |
| 2 | 2026-10-10 | "Create the following structure in the repo for AI configuration" (CLAUDE.md, settings, agents, commands, docs) | Added the agent harness. |
| 3 | 2026-10-11 | "Remove the scripts, I don't need them" | Dropped the formatting and test-running hooks; `settings.json` now holds permissions only. |
