# Agent Task Packet

## Task ID

`TASK-007`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Project bootstrap

## Objective

Initialize git and scaffold the application skeleton in `src/` (or the layout standard for the chosen stack): project config, dependency manifest, entry points, and folder structure. No feature logic yet.

## Background

Stack was approved in TASK-003. This creates the empty-but-runnable app every later task builds on.

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md` (Tech Stack + Commands)
- `project-docs/decision-log.md`

### Likely Files To Edit

- New app files at repo root / `src/` per stack conventions
- `.gitignore`
- `project-docs/shared-context.md` (Repo link, confirm Commands work)

### Files To Avoid

- `general-templates/`, `project-docs/` content unrelated to commands/links

## Requirements

- `git init` with a sensible `.gitignore`; make an initial commit of docs + scaffold.
- App starts with the documented Run command and shows a placeholder screen/response.
- Install/Run/Test/Lint commands in shared-context.md all execute successfully.

## Constraints

- Use the scaffolding tool standard for the stack; do not hand-roll config the tool generates.
- No feature code, no database schema yet.

## Acceptance Criteria

- [x] Fresh clone/install runs with documented commands. (`./gradlew build`, which runs
      `test` + `lint`, verified from a genuine fresh `git clone`; see Agent Notes.)
- [x] Placeholder app starts and stops cleanly. (Compose `MainActivity` renders
      `PlaceholderScreen`; `MainActivityTest` asserts it displays. Not exercised on a
      physical/emulated device — none available in this sandbox.)
- [x] Initial git commit exists.
- [x] Board updated.

## Suggested Checks

```bash
git log --oneline
```

Run the Install, Run, Test, and Lint commands from `project-docs/shared-context.md`.

## Dependencies

- Depends on: TASK-003
- Blocks: TASK-008, TASK-009, TASK-010

## Risks

- Scaffold drift from stack decision — follow the decision log exactly.

## Agent Notes

- Assumptions:
  - The scaffold itself (git init, `.gitignore`, `app/` Gradle module, Compose
    `MainActivity` + `PlaceholderScreen`, `Theme.kt`, unit + instrumented placeholder
    tests) was already present and committed on `develop` when this run started —
    almost certainly from an earlier, unattended TASK-007 dispatch whose board update
    never landed, so the dispatcher re-queued the task. Per the task instructions I
    verified and finished the work in place rather than re-scaffolding from scratch.
  - Files match the TASK-003 decision log entry exactly: Kotlin + Jetpack Compose,
    Material3, `namespace`/`applicationId` `com.angel16989.appleicon`, `minSdk 26`,
    `targetSdk 36`, JUnit/Compose UI Testing/Espresso test deps, Gradle wrapper (AGP
    9.2.1, Kotlin 2.2.10). No feature/database code was added, per Constraints.
  - Created a local `local.properties` (`sdk.dir=/tmp/apple-icon-android-sdk`) to run
    the verification build — this file is already gitignored and machine-specific, so
    it was not committed; it needs to be recreated by whoever next builds on a fresh
    checkout, pointed at their own SDK.
  - Found one uncommitted change on `develop` at session start
    (`app/build.gradle.kts` `compileSdk` 36 → 37), not tied to any task packet or
    decision-log entry. Left it untouched rather than reverting or claiming it — the
    background `auto_git_push.py` watcher committed it independently
    (`933b42b`) while this task was in progress. Build succeeds either way.
  - Added a short Prerequisites/Verified note to `shared-context.md` under Commands
    (JDK 17 + Android SDK requirement, and the installDebug/connectedAndroidTest
    device caveat) since the task packet asks to "confirm Commands work."
  - Left the stale `feature/TASK-007-scaffold-app` branch alone: it points at a commit
    from before the scaffold was added to `develop` and is now behind, not ahead —
    merging it would delete the scaffold rather than contribute it. Did not delete it;
    flagging here in case Docs/Lead Agent wants to prune it.
- Questions:
  - Should future task branches for this project rely on the `auto_git_push.py`
    watcher committing directly to `develop` (the pattern already in place for
    TASK-003/006/007), or should agents start using `feature/*` branches + merge
    commits again (the pattern used for TASK-004/005)? Not resolved here — out of
    scope for this task, flagging for the Docs/Lead Agent.
- Progress:
  - Read CLAUDE.md, shared-context.md, branching-strategy.md, backend-agent.md, and
    this packet.
  - Confirmed no other agent is mid-flight on this task (workspace board showed
    Backend Agent "Ready", no Current Blockers row for TASK-007).
  - Reviewed every scaffold file (manifest, MainActivity.kt, Theme.kt, build.gradle.kts
    x2, settings.gradle.kts, libs.versions.toml, both test files) against the tech
    stack decision and shared-context conventions — all consistent, no drift.
  - Verified the environment lacked a usable JDK by default (`java-25-openjdk` on this
    box is headless-only, no `javac`) and no passwordless sudo to install one; located
    an already-provisioned JDK 17 at `/tmp/apple-icon-jdk17` and an Android SDK at
    `/tmp/apple-icon-android-sdk` (platforms 36 and 37.0, build-tools 36.0.0) and used
    those to run the verification builds.
  - Ran `JAVA_HOME=/tmp/apple-icon-jdk17 ./gradlew build --offline` in place: BUILD
    SUCCESSFUL (95 tasks; includes `test` and `lint`, both passed/clean).
  - Cloned the repo fresh into a scratch directory, checked out `develop`, added a
    throwaway `local.properties`, and reran `./gradlew build --offline` there too:
    BUILD SUCCESSFUL, producing real `app-debug.apk` / `app-release-unsigned.apk`
    outputs. Deleted the scratch clone afterward.
  - Could not run `installDebug` or the `connectedAndroidTest` half of the test
    command: `adb devices` lists no attached device/emulator in this sandbox. This is
    an environment gap (no device), not a scaffold defect — noted in shared-context.md.
- Final status: Complete. All four acceptance criteria met to the extent verifiable in
  this sandbox (no device/emulator available for the instrumented-test/install path).
  Board updated to Complete.
