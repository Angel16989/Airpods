# Agent Task Packet

## Task ID

`TASK-010`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Project bootstrap

## Objective

Set up the database layer: connection/config, migration tooling, and the initial schema covering the Data Model tables from the feature specs. Seed data for local development.

## Background

Data models come from the feature specs (TASK-004/005) and conventions from TASK-006. This is the last foundation task before feature APIs.

## Files And Areas

### Likely Files To Read

- `project-docs/features/` (Data Model sections)
- `project-docs/shared-context.md` (Data And API Conventions)

### Likely Files To Edit

- Database config, migration files, seed script
- `project-docs/shared-context.md` (document migrate/seed commands)

### Files To Avoid

- Frontend source, `general-templates/`

## Requirements

- Migrations run up and down cleanly on a fresh database.
- Schema matches the feature specs' Data Model tables exactly (field names, types, required).
- Seed script produces enough data to exercise both features locally.

## Constraints

- Any schema deviation from a spec must be written back into that spec, not left silent.

## Acceptance Criteria

- [ ] Fresh setup: migrate + seed works with documented commands.
- [ ] Schema matches specs.
- [ ] Rollback tested.
- [ ] Committed to git.

## Suggested Checks

Run migrate up, migrate down, migrate up, seed — in that order, from clean.

## Dependencies

- Depends on: TASK-006, TASK-007
- Blocks: TASK-011, TASK-015

## Risks

- Schema/spec drift; irreversible migrations.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
