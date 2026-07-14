# Agent Task Packet

## Task ID

`TASK-008`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Project bootstrap

## Objective

Set up the testing and linting toolchain: test runner configured with one passing example test per layer (unit + one integration or component test), linter/formatter configured, and the exact commands documented in shared-context.

## Background

Every downstream task's acceptance criteria include "tests or checks pass" — this task makes that possible.

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md`
- Scaffold from TASK-007

### Likely Files To Edit

- Test/lint config files
- Example test files
- `project-docs/shared-context.md` (Commands + Testing Conventions sections)

### Files To Avoid

- `general-templates/`

## Requirements

- Test command runs and passes with at least 2 example tests.
- Lint command runs clean on the scaffold.
- Testing Conventions section in shared-context.md filled in (no TBDs).

## Constraints

- Use stack-default tools unless the decision log says otherwise.

## Acceptance Criteria

- [x] Test and lint commands pass from a clean install.
- [x] Testing Conventions documented.
- [x] Committed to git.

## Suggested Checks

Run the Test and Lint commands from `project-docs/shared-context.md`.

## Dependencies

- Depends on: TASK-007
- Blocks: TASK-011, TASK-012

## Risks

- Flaky example tests would erode trust in every later QA pass — keep them trivial and deterministic.

## Agent Notes

- Assumptions:
  - Test runner and 2 example tests (`PlaceholderUnitTest` unit test, `MainActivityTest` Compose instrumented test) already existed from TASK-007 scaffolding and satisfied the "at least 2 example tests" requirement without new test code.
  - Android Gradle Plugin's built-in `lint` task was already passing per TASK-007; "linter/formatter configured" was read as needing a Kotlin style tool (ktlint), since AGP lint alone doesn't cover Kotlin code style/formatting.
  - Added `org.jlleitschuh.gradle.ktlint` (v14.2.0) as the one new dependency this task needed; logged in `decision-log.md` per the "new libraries need a decision-log entry" rule.
- Questions: None.
- Progress:
  - 2026-07-14: Branched `feature/TASK-008-test-lint-setup` off `develop`. Re-provisioned a local Android SDK (platform 37.0 + build-tools 37.0.0) and a JDK 17 toolchain in the sandbox (both ephemeral, wiped between sessions) to actually run Gradle rather than assume. Confirmed baseline `./gradlew build` passed before any change.
  - Added ktlint plugin via the version catalog (`gradle/libs.versions.toml`), applied it in the root and `app` build files, and added an `android.set(true)` ktlint config block in `app/build.gradle.kts`.
  - `ktlintCheck` found 2 real style violations (multiline `Modifier`/`lightColorScheme` expressions not starting on a new line) and 3 false-positive Composable `PascalCase` naming flags. Ran `ktlintFormat` to auto-fix the wrapping issues, and added `.editorconfig` with `ktlint_function_naming_ignore_when_annotated_with = Composable` to correctly whitelist the Compose naming convention instead of disabling the rule outright.
  - Re-ran full `./gradlew build`: ktlintCheck, AGP lint, and unit tests all pass clean.
  - Documented the `ktlintCheck`/`ktlintFormat` commands and a new Testing Conventions section in `shared-context.md`.
  - Worked concurrently in the same shared working tree as the Frontend Agent (TASK-009) and Backend Agent (TASK-010); staged only ktlint-related hunks from shared files (`app/build.gradle.kts`, `gradle/libs.versions.toml`) to avoid bundling their in-progress, uncommitted changes into this commit.
- Final status: Complete. Merged into `develop`.
