# Agent Task Packet

## Task ID

`TASK-004`

## Assigned Agent

Agent name: Docs Agent

## Feature

Feature name: Core Feature (name it properly in this task)

## Objective

Write the full spec for the product's single most important feature: copy `general-templates/feature-boilerplate.md` to `project-docs/features/<core-feature-name>.md` and fill in every section. Register it in `project-docs/features/feature-directory.md`.

## Background

TASK-002 defines what the product is. The core feature is the one flow the product cannot ship without. All build tasks (TASK-011 to TASK-014) implement this spec.

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md`
- `general-templates/feature-boilerplate.md`

### Likely Files To Edit

- `project-docs/features/<core-feature-name>.md` (new, copied from template)
- `project-docs/features/feature-directory.md`

### Files To Avoid

- `general-templates/` (copy from it, never edit it)

## Requirements

- Every template section filled: user flow, requirements, UI notes, data model, API contract, permissions, validation, errors, testing plan, acceptance criteria.
- Confirm the feature choice with the user before writing the full spec.
- Remove or replace `project-docs/features/example-feature.md` references once a real feature exists.

## Constraints

- Spec only what fits release v0.1; push extras to Nice To Have.
- Do not start implementation.

## Acceptance Criteria

- [ ] Feature spec exists with no empty sections.
- [ ] User confirmed the feature scope.
- [ ] Feature directory updated (core feature added, Example Feature removed or marked replaced).

## Suggested Checks

```bash
grep -rn "TBD\|Example Feature" project-docs/features/
```

## Dependencies

- Depends on: TASK-002
- Blocks: TASK-006, TASK-011, TASK-012

## Risks

- A vague spec here multiplies into rework across six downstream tasks.

## Agent Notes

- Assumptions: TASK-002 now defines Apple Icon as a native Android utility for AirPods users. The core feature is AirPods BLE detection plus an iOS-style popup with animated AirPod visual and battery percentage.
- Questions:
  - Which AirPods models should be supported first in the MVP?
  - What fallback should the popup use if Android overlay permissions are unavailable or denied?
- Progress: Started review on 2026-07-12. Confirmed required template and dependencies. Product/core feature blocker cleared by TASK-002 completion.
- Final status: Ready for full core feature spec.
