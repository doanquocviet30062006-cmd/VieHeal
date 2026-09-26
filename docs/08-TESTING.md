# Test strategy

## Verified baseline

**[IMPLEMENTED]** Eight JUnit/Testcontainers suites cover database smoke, patient, practitioner, service catalog, scheduling, appointment, queue and encounter. They exercise real PostgreSQL migrations, MVC endpoints, JWT test tokens, permissions, tenant isolation, constraints and workflow conflicts. On 2026-09-26, `gradlew.bat --gradle-user-home .gradle-user test --rerun-tasks` executed 136 tests: 0 failures, 0 errors, 0 skipped (`backend/build/test-results/test`). This result describes the inspected commit and must be rerun for the final submission build.

## Target pyramid

| Layer | Scope | Gate |
|---|---|---|
| Backend domain/unit | transitions and validation | fast on every change |
| Backend integration | controller→security→PostgreSQL, Flyway | Testcontainers in CI |
| Android unit | mappers, validators, use cases, reducers | deterministic |
| Android ViewModel | event→state, cancellation, restoration | Loading/Empty/Error/Success |
| Room/repository | TTL, invalidation, scope cleanup, conflicts | migration and integration tests |
| Navigation/UI | protected routes, role visibility, accessibility | Compose tests |
| Contract/E2E | Keycloak + API + Android critical journey | staging gate |
| AI evaluation | safety, schema, grounding, failure | 15-scenario suite |

No final evidence may cite unexecuted tests as passing. Store command, commit, environment, timestamp and report artifact. Backend command: `backend/gradlew.bat test`. Android commands will be defined when its Gradle project exists. AI scenarios are specified in `ai/AI-EVALUATION.md` with `NOT EXECUTED` results.

## Critical journeys

Authenticate; select organization/facility; load appointments; create/reschedule; check in; call/start service; start encounter; draft/edit note; complete encounter; separately complete queue and appointment; handle 400/401/403/404/409/5xx/offline. Verify no cross-tenant disclosure and no sensitive data in logs or failure reports.
