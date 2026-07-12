# Branching Strategy

Use this workflow for agents and human contributors.

## Permanent Branches

| Branch | Purpose | Rules |
| --- | --- | --- |
| `main` | Production branch. | Only release merges and urgent hotfixes land here. |
| `develop` | Integration branch for daily development. | Features and routine bug fixes merge here before release. |

## Temporary Supporting Branches

| Pattern | Purpose | Start From | Merge Back To | Example |
| --- | --- | --- | --- | --- |
| `feature/*` | Specific new features or tasks. | `develop` | `develop` | `feature/login-page` |
| `bugfix/*` | Routine bugs found during development or testing. | `develop` | `develop` | `bugfix/broken-link` |
| `hotfix/*` | Critical production bugs. | `main` | `main` and `develop` | `hotfix/payment-crash` |
| `release/*` | Prepare a production release. | `develop` | `main` and `develop` | `release/v0.1` |

## Agent Workflow

1. Start by updating local refs.

```bash
git fetch origin
```

2. Create a task branch from `develop` for normal work.

```bash
git switch develop
git pull origin develop
git switch -c feature/TASK-000-short-name
```

3. Commit scoped changes on the task branch.

```bash
git status
git add <files>
git commit -m "Complete TASK-000 short description"
```

4. Merge the task branch back into `develop` after review.

```bash
git switch develop
git pull origin develop
git merge --no-ff feature/TASK-000-short-name
git push origin develop
```

## Release Workflow

1. Create a release branch from `develop`.

```bash
git switch develop
git pull origin develop
git switch -c release/v0.1
```

2. Only apply release fixes and documentation updates.
3. Merge the release branch into `main`.
4. Merge `main` back into `develop`.

## Hotfix Workflow

1. Create the hotfix branch from `main`.

```bash
git switch main
git pull origin main
git switch -c hotfix/short-description
```

2. Fix only the urgent production issue.
3. Merge into `main`.
4. Merge the same fix back into `develop`.

## Naming Rules

- Use lowercase branch names.
- Use hyphens between words.
- Include a task ID when one exists.
- Keep names short and specific.
