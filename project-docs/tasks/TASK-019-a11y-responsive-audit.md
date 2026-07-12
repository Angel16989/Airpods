# Agent Task Packet

## Task ID

`TASK-019`

## Assigned Agent

Agent name: Frontend Agent (QA Agent verifies)

## Feature

Feature name: Release v0.1

## Objective

Accessibility and responsive polish pass across the whole app: keyboard navigation, focus states, labels/alt text, color contrast, text overflow, mobile layout — per the Design section of the release checklist.

## Background

Feature work handled per-screen basics; this is the whole-app sweep the release checklist requires.

## Files And Areas

### Likely Files To Read

- `project-docs/releases/release-checklist.md` (Design section)
- Feature specs (Accessibility notes)

### Likely Files To Edit

- Frontend source and tests

### Files To Avoid

- Backend source, `general-templates/`

## Requirements

- Every interactive element reachable and operable by keyboard with visible focus.
- Form fields labelled; images have alt text; contrast meets WCAG AA.
- No text overflow or broken layout at 320px–1440px widths.

## Constraints

- Visual polish only — no behavior changes.

## Acceptance Criteria

- [ ] Keyboard-only walkthrough of both features succeeds.
- [ ] Contrast and label audit recorded in this packet.
- [ ] Mobile and desktop layouts checked at 320/768/1440.
- [ ] QA Agent verified and signed off in this packet.
- [ ] Committed to git.

## Suggested Checks

Walk both flows keyboard-only; use browser devtools device toolbar at 320px.

## Dependencies

- Depends on: TASK-018
- Blocks: TASK-021

## Risks

- Treating this as optional — it is a release-checklist requirement.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
