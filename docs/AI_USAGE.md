# AI-Assisted Development

How AI tooling was set up and used for this take-home, as requested in requirement 6 of the brief.

## Tools

| Tool | Purpose |
|------|---------|
| [Claude Code](https://claude.com/claude-code) (CLI) | Main coding agent: planning, implementation, tests, refactoring, reviews |
| Claude Opus 5.5 (`claude-opus-5-5`), then Claude Sonnet 5.5 (`claude-sonnet-5-5`) | Models behind Claude Code (switched part-way through) |
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

The agent wrote the code; I set the requirements, made the design decisions and reviewed and ran the result. The prompts
behind each row are in [`prompts-log.md`](prompts-log.md).

| Area | AI involvement | Why / human role |
|------|----------------|------------------|
| Repo cleanup (removing non-Java languages) | Fully AI-executed | Mechanical; reviewed the diff |
| AI harness setup (this structure) | AI-drafted, human-reviewed | Boilerplate-heavy; rules and constraints checked against the brief |
| Dependencies and build configuration (Spring Boot, Hibernate, Liquibase, Lombok, Spotless, ArchUnit, springdoc, OpenAPI generator) | AI chose versions and wrote the `pom.xml` and properties | Tedious and version-sensitive: the agent checked Maven Central and Java 17 compatibility (e.g. the newest google-java-format that runs on 17). I chose which tools to use |
| PostgreSQL through Testcontainers, for the app and for tests | AI implemented and launched the app to verify | I asked for the container to start with the main application and chose the default `local` profile |
| Hexagonal structure and ArchUnit rules | AI drafted packages and rules; proved the rules fail on deliberate violations | I decided the layering and moved classes where I disagreed (application class in the root package, adapter and mapper placement) |
| Characterization tests for `TimeDepositCalculator` | AI wrote 32 tests plus a golden-master test comparing against the legacy algorithm | Safety net for the refactor; I asked for exact rule coverage (30/31, 45/46, 365/366) |
| Interest calculation refactor | AI proposed Strategy, Template Method, registry, switch and ServiceLoader options and implemented the chosen one | I made the design calls: Template Method, `domain.interest`, no registry, no static helpers, verb-first names, a switch for selection. The legacy tests passing unchanged is the check |
| Database schema, entities and relationships | AI wrote the Liquibase changesets, entities and cascade rules | I chose Liquibase, snake_case names, the one-to-many mapping with cascade, and `LocalDateTime` |
| Seed data | AI wrote the changesets and verified them by launching the app | I specified the boundary cases (day 15, 29, 399, 44) and that tests must not use the seed |
| `POST` balance-update endpoint | AI planned (`/plan`) and implemented it | I set the path, `204`, the `V1` controller suffix, updates at database level and contract-first |
| `GET` endpoint and response DTO | AI implemented it; running the app exposed a time-zone bug that it then reproduced with a test and fixed | I set the scope and required DTO, Swagger and integration tests; I replaced the JPQL read with a derived query and `@EntityGraph` |
| OpenAPI contract and Swagger UI | AI wrote the YAML, generator setup and Swagger configuration | I required a contract-first API with Swagger documentation |
| Integration tests (Testcontainers, MockMvc) | AI wrote them for persistence, both endpoints and the contract | I asked for integration coverage of each endpoint |
| Documentation (README running instructions, assumptions, test data, this file) | AI-drafted from the repository and the running database | I review and edit the text |

### Where I corrected or overrode the AI

- Removed the static helper methods and the `InterestStrategyRegistry` class after questioning why they existed.
- Simplified the interest calculation to a single `switch` in the `TimeDepositCalculator` instead of a registry of strategies.
- Required verb-first method names in the new domain classes.
- Replaced the JPQL read query with a Spring Data derived method.
- Tried `LocalDate` for withdrawals, then reverted to `LocalDateTime`.
- Kept the response mapper in the web adapter: the agent flagged that returning the web DTO from the service would break the hexagonal rule, and I chose to keep the layering.
- Kept the one-update-per-deposit loop instead of batching.

### How the output was verified

- `mvn verify`: unit tests, ArchUnit rules and Testcontainers integration tests.
- A golden-master test that compares the refactored calculator with the legacy algorithm across plans, days and balances.
- Running the application, calling both endpoints from Swagger and checking the database contents.
- Reviewing each diff in the IDE and committing atomically myself.
- Manual testing of APIs from Swagger UI