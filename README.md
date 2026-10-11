# Time Deposit Refactoring Kata - Take-Home Assignment

## Running Instructions

### Assumptions I made

I wasn't sure what was meant by shared classes, so I left them at root level and didn't change their package. The domain is considered the 2 original classes + domain package which is isolated from the rest of the code.

I assumed everytime POST for updating balance is called, the interest is calculated for 1 month.



### Prerequisites
- Java 17 or newer
- Maven 3.8+
- Docker running (PostgreSQL runs in a Testcontainers container, so no local database is needed)

### Run the application
```bash
cd java
mvn spring-boot:run
```
You can also run `org.ikigaidigital.TimeDepositApplication` from your IDE. Run `mvn compile` once first (or reload the Maven project): the API interface and DTOs are generated from the OpenAPI contract at build time.

On startup the `local` profile (the default) starts a `postgres:17-alpine` container, Liquibase creates the schema and loads the test data, and the API is served on **http://localhost:8080**. Stopping the application removes the container and its data; the next start begins from the test data again.

To use your own PostgreSQL instead, activate any other profile (e.g. `SPRING_PROFILES_ACTIVE=prod`) and set `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`.

### Try the API from Swagger
Open **http://localhost:8080/swagger-ui.html** (the contract itself is at `/openapi/time-deposit-api.yaml`).

### Test data
Loaded by Liquibase changesets `003-seed-time-deposits-and-withdrawals` and `004-adjust-seed-time-deposits-to-boundary-cases` (context `seed`). Ids are generated, so on a fresh database they are 1 to 9.

**Time deposits** (3 per plan, chosen to exercise every interest rule):

| id | Plan | Days | Balance | After one update | Why |
|---|---|---|---|---|---|
| 1 | basic | 15 | 1,000.00 | 1,000.00 | within the first 30 days, no interest |
| 2 | basic | 45 | 2,500.00 | 2,502.08 | 1% yearly, one month |
| 3 | basic | 200 | 10,000.00 | 10,008.33 | 1% yearly, one month |
| 4 | student | 29 | 500.00 | 500.00 | within the first 30 days, no interest |
| 5 | student | 180 | 1,500.00 | 1,503.75 | 3% yearly, one month |
| 6 | student | 399 | 3,000.00 | 3,000.00 | past one year, no interest |
| 7 | premium | 44 | 5,000.00 | 5,000.00 | interest only starts after day 45 |
| 8 | premium | 90 | 12,000.00 | 12,050.00 | 5% yearly, one month |
| 9 | premium | 365 | 25,000.00 | 25,104.17 | 5% yearly, one month |

**Withdrawals** (3 per time deposit, 27 in total):

| Deposit | Withdrawal 1 | Withdrawal 2 | Withdrawal 3 |
|---|---|---|---|
| 1 | 100.00 on 2026-06-01 09:00 | 50.00 on 2026-07-15 14:30 | 25.00 on 2026-08-20 11:15 |
| 2 | 200.00 on 2026-05-10 10:00 | 120.50 on 2026-07-02 16:45 | 75.00 on 2026-09-01 08:30 |
| 3 | 1,000.00 on 2026-03-05 12:00 | 500.00 on 2026-05-18 09:20 | 250.75 on 2026-08-30 15:10 |
| 4 | 40.00 on 2026-06-12 13:00 | 15.00 on 2026-07-01 10:30 | 10.25 on 2026-09-10 17:00 |
| 5 | 150.00 on 2026-04-22 11:00 | 90.00 on 2026-06-30 09:45 | 60.50 on 2026-08-14 14:00 |
| 6 | 300.00 on 2026-01-15 10:15 | 225.00 on 2026-04-01 12:30 | 100.00 on 2026-07-25 16:20 |
| 7 | 500.00 on 2026-06-05 09:00 | 250.00 on 2026-07-20 13:40 | 125.00 on 2026-09-15 10:10 |
| 8 | 1,200.00 on 2026-04-10 15:00 | 800.00 on 2026-06-25 11:30 | 400.00 on 2026-08-28 08:50 |
| 9 | 2,500.00 on 2026-02-14 10:00 | 1,750.00 on 2026-05-30 14:15 | 900.00 on 2026-09-05 12:45 |

### AI-assisted development
This project was built with an AI coding assistant (Claude Code):
- [`docs/AI_USAGE.md`](docs/AI_USAGE.md) describes how AI was used: the tools, the setup (`CLAUDE.md` rules, subagents and commands in `.claude/`), the workflow, and which parts were AI-assisted and why.
- [`docs/prompts-log.md`](docs/prompts-log.md) lists the prompts used during development and the decisions that came out of them.

## XA Bank Time Deposit

### Context
A junior developer implemented domain logic for a time deposit system but did not complete the API functionality. Your task is to refactor the existing codebase to implement all required functionalities based on the provided business requirements, ensuring no breaking changes occur.

### Requirements

1. **API Endpoints**:
    - Create a RESTful API endpoint to update the balances of all time deposits in the database.
    - Create a RESTful API endpoint to retrieve all time deposits.
        - The GET endpoint should return a list of all time deposits with the following schema:
            - `id`
            - `planType`
            - `balance`
            - `days`
            - `withdrawals`

2. **Database Setup**:
    - Store all time deposit plans in a database.
    - Define the following tables:
        - `timeDeposits`:
            - `id`: Integer (primary key)
            - `planType`: String (required)
            - `days`: Integer (required)
            - `balance`: Decimal (required)
        - `withdrawals`:
            - `id`: Integer (primary key)
            - `timeDepositId`: Integer (foreign key, required)
            - `amount`: Decimal (required)
            - `date`: Date (required)

3. **Interest Calculation**:
    - Implement logic to calculate monthly interest based on the plan type:
        - **Basic Plan**: 1% interest
        - **Student Plan**: 3% interest (no interest after 1 year)
        - **Premium Plan**: 5% interest (interest starts after 45 days)
    - No interest is applied for the first 30 days for any existing plans.

4. **Refactoring Constraints**:
    - Do not introduce breaking changes to the shared `TimeDeposit` class or modify the `updateBalance` method signature.
    - Ensure the design is extensible to accommodate future complexities in interest calculations.

5. **Code Quality**:
    - Adhere to SOLID principles, design patterns, and clean code practices where applicable.

6. **AI-Assisted Development**:
    - Set up an AI harness or agent workflow and use it throughout the development for this take-home exercise.
    - Briefly document the tools and setup used (e.g., LLMs, coding assistants, agentic frameworks, configuration).
    - Ensure your AI setup is practical and reproducible.
    - Include any custom rules, system prompts, or agent configurations used.
    - Include a brief summary of which parts of the solution were AI-assisted and why.

### Important Guidelines
- The existing `TimeDepositCalculator.updateBalance` method is functioning correctly. Ensure its behavior remains unchanged after refactoring.
- The final solution must include **exactly two API endpoints**. Do not develop additional endpoints.
- **Do not** create a pull request or a new branch in the ikigai-digital repository. Instead, fork the repository into your own GitHub repository and develop the solution there.
- Handling invalid input or exceptions is not required.
- Use any tools, frameworks, or libraries you find suitable.
- In case of ambiguity, make logical assumptions and justify them in code comments.

### Preferred Stack
- Use an OpenAPI Swagger contract.
- Embrace Hexagonal Architecture.
- Follow atomic commit practices.
- Utilize testcontainers.
- Leverage AI-assisted development tools for code generation, testing, and refactoring.

### Submission Instructions
- Provide clear instructions on how to trigger the endpoints using the Swagger contract.
- Email the link to your public GitHub repository.