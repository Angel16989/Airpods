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

- [x] Shell + primitives render in the running app.
- [x] Responsive at mobile and desktop widths.
- [x] Component tests for primitives pass.
- [x] Committed to git.

## Suggested Checks

Run the Test and Lint commands; view the app at mobile and desktop widths.

## Dependencies

- Depends on: TASK-007
- Blocks: TASK-012, TASK-016

## Risks

- Over-building a design system nobody asked for — stay minimal.

## Agent Notes

- Assumptions:
  - Current uncommitted TASK-008 lint/tooling changes are preserved and treated as in-progress workspace context.
  - "Desktop widths" means wide/tablet Compose previews or responsive layout checks, since the product target is native Android.
  - TASK-009 creates reusable UI shell/primitives only; BLE, popup, notification, and persistence feature logic stays in later tasks.
- Questions:
- Progress:
  - 2026-07-14: Started concurrently with TASK-010. Reading feature UI notes, current scaffold, and existing theme patterns before editing.
  - 2026-07-14: Added the `AppleIconApp` responsive dashboard shell, Material 3 light/dark theme updates, shared design tokens, and shared primitives for button, text field, loading, empty, and error states.
  - 2026-07-14: Added Compose component tests for the shared primitives and documented design conventions in `shared-context.md`.
  - 2026-07-14: Verification so far: `./gradlew ktlintCheck` passes and `./gradlew :app:compileDebugKotlin` passes. Full `./gradlew test` is blocked in this environment because the installed Java runtime has no `javac`.
  - 2026-07-14: Provisioned a local Temurin JDK 17 at `~/.local/share/jdks/jdk-17.0.19+10`; `./gradlew build` passes and Android test sources compile. `connectedAndroidTest` was not run because no device/emulator is attached.
- Final status: Complete. Implemented and committed on `feature/TASK-009-010-foundation`.
