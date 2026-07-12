# Agent Task Packet

## Task ID

`TASK-009`

## Assigned Agent

Agent name: Frontend Agent

## Feature

Feature name: Project bootstrap

## Objective

Build the base UI foundation: app shell (layout, navigation placeholder), design tokens (colors, spacing, typography), and shared primitives (button, input, loading indicator, error/empty state components).

## Background

Feature UIs (TASK-012, TASK-016) should compose these primitives instead of inventing styles per screen.

## Files And Areas

### Likely Files To Read

- `project-docs/features/` (UI Notes sections)
- `project-docs/shared-context.md` (Design Conventions)

### Likely Files To Edit

- Frontend source under the scaffold from TASK-007
- `project-docs/shared-context.md` (Design Conventions, fill any TBDs)

### Files To Avoid

- Backend source, `general-templates/`

## Requirements

- App shell renders with placeholder content on desktop and mobile widths.
- Shared components each handle their states (e.g. button disabled/loading).
- Design tokens defined once and used by all primitives.

## Constraints

- Keep it minimal — only primitives the two feature specs actually need.
- No feature logic.

## Acceptance Criteria

- [ ] Shell + primitives render in the running app.
- [ ] Responsive at mobile and desktop widths.
- [ ] Component tests for primitives pass.
- [ ] Committed to git.

## Suggested Checks

Run the Test and Lint commands; view the app at mobile and desktop widths.

## Dependencies

- Depends on: TASK-007
- Blocks: TASK-012, TASK-016

## Risks

- Over-building a design system nobody asked for — stay minimal.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
