# Prompts & Decisions Log

Short log of the key prompts given to the agent and the decisions that came out of them. Newest last.

| # | Date | Prompt (summary) | Outcome / decision |
|---|------|------------------|--------------------|
| 1 | 2026-10-10 | "Remove every other language besides java" | Deleted the C#, Kotlin, Python and TypeScript starter projects; only `java/` remains. |
| 2 | 2026-10-10 | "Create the following structure in the repo for AI configuration" (CLAUDE.md, settings, agents, commands, docs) | Added the agent harness. |
| 3 | 2026-10-11 | "Remove the scripts, I don't need them" | Dropped the formatting and test-running hooks; `settings.json` now holds permissions only. |
| 4 | 2026-10-11 | "Setup spring boot, hibernate, testcontainers dependencies. Keep in mind I am using java 17" | Spring Boot 4.1.1 (Java 17 baseline) as parent with webmvc, data-jpa (Hibernate 7.4), PostgreSQL driver, Testcontainers 2.0.5 via `spring-boot-testcontainers`. Integration tests named `*IT` and run by Failsafe on `mvn verify`. Added `TimeDepositApplication` and a Postgres-container smoke test. |
| 5 | 2026-10-11 | "Create the application properties" | Added `application.properties`: PostgreSQL datasource from env vars with local defaults, `ddl-auto=validate` (schema owned by SQL migrations), `open-in-view=false`, UTC JDBC time zone. |
| 6 | 2026-10-11 | "Only create the package structure for ports and adapters" | Added `application.port.in`, `application.port.out`, `adapter.in.web`, `adapter.out.persistence` (each with a bare `package-info.java`). Decided `TimeDeposit` is the domain entity, mapped to a separate JPA entity in the persistence adapter. `TimeDeposit` and `TimeDepositCalculator` stay in the root package. |
