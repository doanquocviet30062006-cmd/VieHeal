# Product requirements

## Actors and goals

| Actor | Primary goal | Authorization principle |
|---|---|---|
| Receptionist | register patients, manage bookings and queue | explicit permission, assigned facility |
| Doctor | review patients, serve encounter, edit note | clinical permissions, assigned scope |
| Nurse | read encounter/note and support workflow | no permission inferred from title |
| Manager/admin | configure staff, services, schedules | organization permissions |
| Patient | future self-service | [PLANNED], separate least-privilege surface |

## Functional requirements

- **FR-01 [IMPLEMENTED backend]** validate JWT and resolve the IAM user by external subject.
- **FR-02 [IMPLEMENTED backend]** CRUD-style create/read/update for patient, practitioner, services, scheduling and appointments; deletion is intentionally not exposed.
- **FR-03 [IMPLEMENTED backend]** check in a scheduled appointment and progress queue `WAITING → CALLED → SERVING → COMPLETED`, with cancellation paths.
- **FR-04 [IMPLEMENTED backend]** start an encounter only from a `SERVING` queue entry whose appointment is `SCHEDULED`; server derives patient and practitioner; uniqueness is enforced for appointment and queue entry.
- **FR-05 [IMPLEMENTED backend]** edit a single SOAP-style clinical note only while encounter rules permit, then complete/cancel the encounter. Encounter completion does not complete appointment or queue.
- **FR-06 [TARGET PRODUCTION DESIGN]** expose these flows using real APIs, deterministic Loading/Empty/Error/Success states, validation and role-aware navigation.
- **FR-07 [TARGET PRODUCTION DESIGN]** draft or summarize clinician documentation through a backend gateway, structured validation, safety rules and explicit acceptance.
- **FR-08 [TARGET PRODUCTION DESIGN]** cache only the minimum authorized operational data and visibly mark stale content; queue clinical writes for retry only after a conflict-safe design is approved.

## Non-functional requirements

- **NFR-01 Security:** OAuth2/OIDC Authorization Code + PKCE for Android; TLS; no secrets in APK; PHI-safe logs.
- **NFR-02 Reliability:** explicit timeouts, idempotency strategy, no blind retry of non-idempotent writes, graceful provider failure.
- **NFR-03 Performance:** define measured budgets after baseline; target responsive cached rendering and instrument p95 backend/AI latency.
- **NFR-04 Accessibility:** screen-reader labels, logical focus, 48dp targets, scalable type, adequate contrast and non-color status cues.
- **NFR-05 Privacy:** data minimization, scoped cache, logout/scope cleanup, retention and backup decisions.
- **NFR-06 Testability:** dependency inversion, fake test doubles only in tests, contract/integration tests against PostgreSQL.

## Acceptance boundary

A mobile feature is complete only if UI, navigation, ViewModel, lifecycle restoration, real repository/API, auth, permission behavior, all relevant states, validation, tests, and secret review pass. `FakeBackend`, fixed login identity, or fixed AI text fails acceptance.
