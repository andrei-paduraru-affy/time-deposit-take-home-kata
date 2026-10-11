# CLAUDE.md

Project rules for AI agents working in this repository. Read this fully before making changes.

## What this is

The XA Bank Time Deposit refactoring kata (see `README.md` for the full brief). The goal is to expose the existing
domain logic through a REST API backed by a database, **without changing the behaviour of the existing calculator**.

- Stack: Java 17, Maven, Spring Boot 4.1 (Spring MVC, Spring Data JPA / Hibernate 7), PostgreSQL,
  Testcontainers 2. All code lives in `java/`.
- Package root: `org.ikigaidigital`.

## Commands

Run from `java/`:

```bash
mvn -q test            # unit tests, *Test classes (must pass before every commit)
mvn -q verify          # + integration tests, *IT classes via Failsafe (Testcontainers needs Docker running)
```

## Hard constraints (never violate)

1. **`TimeDeposit` is a shared class.** Do not rename, remove, or change the signature of its constructor, getters
   or `setBalance`. Adding things is allowed only if it is strictly non-breaking.
2. **`TimeDepositCalculator.updateBalance(List<TimeDeposit>)`** keeps its exact signature and observable behaviour,
   including rounding (`HALF_UP`, scale 2, applied to the interest only) and the use of `Double` balances.
   Before refactoring it, lock its behaviour with characterization tests covering every plan type and the day
   boundaries (30/31, 45/46, 365/366).
3. **Exactly two API endpoints**: one to update all balances, one to list all time deposits
   (`id`, `planType`, `balance`, `days`, `withdrawals`). Never add a third (no health/debug/admin endpoints
   in our own controllers).
4. Database schema follows the README: `timeDeposits` and `withdrawals` tables with the listed columns.
5. Do not add input validation or exception handling beyond what is needed — the brief says it is not required.

## Architecture

- **Hexagonal (ports & adapters)**:
  - `domain` — pure Java, no framework imports. Holds `TimeDeposit`, interest rules, domain services.
  - `application` — use cases and port interfaces (inbound + outbound).
  - `adapter.in.web` — REST controllers generated from / conforming to the OpenAPI contract.
  - `adapter.out.persistence` — JPA/JDBC entities and repositories; map to/from domain objects.
- Dependencies point inward only. The domain must not depend on Spring, JPA or web types.
- **Contract-first OpenAPI**: the spec is the source of truth for the API; keep it in sync with the code.
- Interest calculation must be extensible: one strategy per plan type (Strategy pattern / registry), so a new plan
  means adding a class, not editing a conditional (Open/Closed).

## Coding conventions

- Formatting: Google Java Style, applied automatically by Spotless (google-java-format) on every Maven build;
  run `mvn spotless:apply` to format without building. Never hand-format against it.
- SOLID, small classes, intention-revealing names, no dead code, no commented-out code, no commented properties, no commented dependencies.
- Use `BigDecimal` for new money code, but convert at the boundary so `updateBalance` behaviour stays identical.
- Where the brief is ambiguous, ask and do not make assumptions without approval.
- Tests: JUnit 5 + AssertJ. Unit tests for domain logic; Testcontainers (PostgreSQL) for persistence and API
  integration tests. Test names describe behaviour (`shouldNotApplyInterestToStudentPlanAfterOneYear`).

## Workflow

- Plan before coding non-trivial changes (`/plan`). Keep the plan short and get approval.
- Write or update tests first when changing behaviour; when refactoring, tests must stay green throughout.
- **Atomic commits**: one logical change per commit, imperative subject line, tests passing at every commit.
- Run `/review` on the diff before committing larger changes.
- Do not push, create branches on the upstream repository, or open PRs unless explicitly asked.
- After a significant prompt or design decision, add a one-line entry to `docs/prompts-log.md`.
