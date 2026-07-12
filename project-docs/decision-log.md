# Decision Log

| Date | Decision | Reason | Owner | Status |
| --- | --- | --- | --- | --- |
| 2026-07-12 | Use `project-docs/` as the active multi-agent workspace. | Keeps active project docs separate from reusable templates. | Docs Agent | Active |
| 2026-07-12 | Keep reusable boilerplates in `general-templates/`. | Templates can be copied into future projects without mixing active task state. | Docs Agent | Active |
| 2026-07-12 | Use `develop` as the integration branch, with `feature/*`, `bugfix/*`, `hotfix/*`, and `release/*` supporting branches. | Gives agents a predictable Git workflow for daily work, releases, and urgent fixes. | Docs Agent | Active |
