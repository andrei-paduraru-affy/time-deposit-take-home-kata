# AI-Assisted Development

How AI tooling was set up and used for this take-home, as requested in requirement 6 of the brief.

## Tools

| Tool | Purpose |
|------|---------|
| [Claude Code](https://claude.com/claude-code) (CLI) | Main coding agent: planning, implementation, tests, refactoring, reviews |
| Claude Opus 5.5 (`claude-opus-5-5`) | Model behind Claude Code |
| IntelliJ IDEA | Manual reading, debugging and final review of every change |

## Setup

Everything the agent uses is committed to the repo, so the setup reproduces by cloning and running `claude` in
the repository root.

```
├── CLAUDE.md                  # project rules / system prompt for the agent
├── .claude/
│   ├── settings.json          # permissions
│   ├── agents/
│   │   ├── test-writer.md     # subagent: writes tests only
│   │   └── reviewer.md        # subagent: reviews diffs, no edits
│   └── commands/
│       ├── plan.md            # /plan → design before coding
│       └── review.md          # /review → runs reviewer on the current diff
└── docs/
    ├── AI_USAGE.md            # this file
    └── prompts-log.md         # short log of key prompts and decisions
```

**Prerequisites:** Claude Code, JDK 17+, Maven 3.8+, Docker (for Testcontainers).

### `CLAUDE.md` — project rules

Loaded automatically into every session. It encodes the kata's hard constraints (no breaking changes to
`TimeDeposit`, `updateBalance` behaviour preserved, exactly two endpoints), the target architecture (hexagonal,
contract-first OpenAPI, Strategy pattern for interest rules), coding and testing conventions, and the workflow
(plan → test → implement → review → atomic commit).

### `.claude/settings.json` — permissions

Maven and read-only git commands are pre-approved; `commit`, `push`, `reset` and `rebase` require
confirmation; force-push, `rm -rf` and reading `.env` files are denied.

### Subagents

- **`test-writer`** — may only edit `java/src/test/`. Writes characterization tests before refactoring and
  behaviour tests for new features.
- **`reviewer`** — read-only. Reviews the diff against the kata constraints, hexagonal boundaries, correctness and
  clean code, and reports findings by severity.

Separating them keeps tests independent from the implementation they verify, and keeps reviews unbiased by the
author's context.

### Slash commands

- **`/plan <change>`** — produces a design (layers, classes, assumptions, test plan, commit plan) without writing
  code, and waits for approval.
- **`/review [range]`** — delegates to the `reviewer` subagent and relays the findings without applying fixes.

## Workflow

1. `/plan` the change and approve or adjust the design.
2. `test-writer` adds characterization or behaviour tests.
3. Main agent implements and runs the tests.
4. `/review` the diff; I decide which findings to act on.
5. I review the change in the IDE and commit atomically.
6. Key prompts and decisions go into [`prompts-log.md`](prompts-log.md).

## What was AI-assisted and why

<!-- Fill in as the work progresses. -->

| Area | AI involvement | Why / human role |
|------|----------------|------------------|
| Repo cleanup (removing non-Java languages) | Fully AI-executed | Mechanical; reviewed the diff |
| AI harness setup (this structure) | AI-drafted, human-reviewed | Boilerplate-heavy; rules and constraints checked against the brief |
| Characterization tests | | |
| Interest calculation refactor | | |
| Database schema and persistence adapter | | |
| OpenAPI contract and REST adapter | | |
| Testcontainers integration tests | | |
| Documentation | | |
