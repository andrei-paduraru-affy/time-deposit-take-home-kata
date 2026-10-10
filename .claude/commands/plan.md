---
description: Design an approach before writing any code
argument-hint: <feature or change to plan>
---

Produce a design plan for: $ARGUMENTS

Do **not** write or edit any code yet. Read `CLAUDE.md`, the relevant parts of `README.md` and the existing code
first, then reply with:

1. **Goal** — one or two sentences.
2. **Constraints touched** — which hard constraints from `CLAUDE.md` apply and how the plan respects them.
3. **Design** — affected hexagonal layers, new/changed classes and interfaces (names + one-line responsibility),
   and any OpenAPI contract or DB schema changes.
4. **Assumptions** — ambiguities in the brief and the decision taken for each.
5. **Test plan** — which tests to add first (unit vs Testcontainers).
6. **Commit plan** — ordered list of atomic commits, each leaving the build green.

Keep it concise. End by asking for approval before implementing.
