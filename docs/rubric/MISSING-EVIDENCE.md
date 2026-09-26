# Prioritized evidence and implementation gaps

Unknown assignments use owner **UNASSIGNED**. A gap closes only when its definition of done and evidence are both satisfied.

## P0 — final product/demo function

| Gap | Owner | Evidence needed | Definition of done |
|---|---|---|---|
| Android project absent | UNASSIGNED | source tree, signed demo build, architecture tests | release-capable app runs critical journey against real API with no production fake dependency |
| Mobile OIDC/PKCE absent | UNASSIGNED | realm/client config, login/logout/session video/tests | public client authenticates; no secret/password in APK; 401 and cleanup verified |
| Core mobile workflow absent | UNASSIGNED | appointment→queue→encounter→note screenshots/API/DB proof | authorized user completes persisted synthetic journey and handles 400/403/409 |
| AI gateway/provider absent | UNASSIGNED | backend source/config, provider trace metadata, review UI | real model called through backend; schema/safety/human review; no mobile key or fake response |
| Organization creation is public | UNASSIGNED | security change and integration reports | unauthenticated 401, normal user 403, authorized platform admin 201, audit evidence |
| Final build/video absent | UNASSIGNED | evidence manifest, AAB/APK, 5–10 min video | video matches exact submitted build/commit and demonstrates required real evidence |

## P1 — major grading evidence

| Gap | Owner | Evidence needed | Definition of done |
|---|---|---|---|
| User survey unexecuted | UNASSIGNED | consent-safe dataset, analysis and limitations | real respondents collected; no fabricated numbers; findings linked to requirements |
| Figma/prototype absent | UNASSIGNED | file/link/export, component library and click-through | required frames/states exist and map to Compose screens |
| Room/cache absent | UNASSIGNED | entities/DAOs/migrations/tests | minimized scoped cache, TTL/invalidation and logout/scope cleanup pass tests |
| Android state evidence absent | UNASSIGNED | tests/screens for Loading/Empty/Error/Success plus permission/offline/conflict | core screens pass state/accessibility checks |
| AI evaluation unexecuted | UNASSIGNED | redacted result table/artifacts/reviewer | all 15 versioned scenarios executed without invented results; failures triaged |
| Final report/slides absent | UNASSIGNED | rendered 40–60 page PDF and 10–14 slide deck | consistent, cited, implementation claims match final commit |
| Project workflow evidence weak | UNASSIGNED | real issues/board/PR/review or honest solo-review records | artifacts exist with real dates/participants; no fabricated teamwork |
| Final test archive absent | UNASSIGNED | backend/Android/AI reports tied to final commit | commands, environment, date, totals and artifacts recorded |

## P2 — production hardening

| Gap | Owner | Evidence needed | Definition of done |
|---|---|---|---|
| Identity/TLS/secrets not production-ready | UNASSIGNED | hardened realm, TLS/DNS, secret rotation and tests | production profiles contain no dev mode/default secret; rotation and auth tests pass |
| CI/CD absent | UNASSIGNED | pipeline runs and protected environment evidence | build/test/scan/migration/staging/deploy gates reproducible |
| Audit/consent behavior absent | UNASSIGNED | migrations/services/policies/tests | approved model implements required capture, access and integrity controls |
| Observability incomplete | UNASSIGNED | dashboards/alerts/runbooks and PHI scan | agreed metrics/logs/traces operate without sensitive payloads |
| Backup/recovery absent | UNASSIGNED | policy, encrypted backups and restore report | RPO/RTO approved; restore drill meets them |
| Threat/privacy/legal reviews absent | UNASSIGNED | signed review records and remediation | mobile/AI/provider/retention/incident decisions approved before production |

## P3 — optional enhancement

| Gap | Owner | Evidence needed | Definition of done |
|---|---|---|---|
| Patient self-service | UNASSIGNED | validated requirements/design/security tests | separate least-privilege client delivered after staff MVP |
| Push/event cache invalidation | UNASSIGNED | architecture, delivery/privacy tests | measured need; no PHI leakage; TTL fallback works |
| Diagnosis/observation/prescription/lab/imaging/billing | UNASSIGNED | domain/API/DB/permission/test evidence | each module meets production definition of done |
| Dark theme/personalization | UNASSIGNED | accessible design/implementation/tests | no displacement of P0/P1 and verified usability value |

## Related documents

[Evidence matrix](RUBRIC-EVIDENCE-MATRIX.md), [Submission checklist](SUBMISSION-CHECKLIST.md), [Implementation order](../00-PROJECT-OVERVIEW.md).
