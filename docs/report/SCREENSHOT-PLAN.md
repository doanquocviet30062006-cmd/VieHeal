# Screenshot plan

All entries are **SCREENSHOT REQUIRED** until captured from the submitted build with synthetic data.

| ID | Capture | Proves |
|---|---|---|
| S01 | Figma flow/components | UI/UX design/prototype |
| S02–S04 | login, scope, dashboard | auth/navigation |
| S05–S08 | appointment list/loading/empty/error/create | CRUD and state coverage |
| S09–S11 | queue before/after and conflict | real workflow |
| S12–S14 | encounter/note/terminal state | clinical boundary |
| S15–S17 | AI request, review/edit, refusal/failure | real AI and safety |
| S18 | permission denied/offline stale | resilience/security |
| S19 | PostgreSQL/API persisted record | system of record |
| S20–S22 | backend/Android/AI test reports | executed testing |
| S23 | Git graph/board/PR | team evidence |
| S24 | deployment/health/dashboard | production readiness |

Record filename, build version, commit, timestamp, scenario, synthetic account/role and rubric row. Crop consistently, redact tokens/URLs if sensitive, and never use real patient data. A mocked visual is design evidence only, never implementation evidence.
