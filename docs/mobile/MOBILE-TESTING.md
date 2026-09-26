# Mobile testing

No Android tests exist because no Android source exists. This is the required **[TARGET PRODUCTION DESIGN] [EVIDENCE GAP]** matrix.

| Target | Tests |
|---|---|
| validators/mappers/use cases | boundary, locale, time-zone and malformed DTO cases |
| repositories | network/cache choice, TTL, invalidation, 401/403/404/409, cancellation |
| Room | DAO queries, migration, scope partition, logout cleanup |
| ViewModels | every event and Loading/Empty/Error/Success plus refresh/offline/conflict |
| navigation | unauthenticated redirect, protected back stack, deep link, logout, process recreation |
| Compose | semantics, accessibility, validation, permission visibility, state screenshots |
| integration | MockWebServer contract and real staging API smoke |
| E2E | login→appointment→queue→encounter→note→AI review |

Use coroutine test dispatchers, Turbine or equivalent StateFlow assertions, in-memory Room for DAO behavior plus migration tests on real files, and Compose test rules. Avoid screenshot-only assertions for business behavior. Test doubles implement repository contracts and live only in test/debug fixtures; final release variants cannot depend on them.

Required state suite for appointment, queue, patient, encounter, note and AI screens: initial loading, empty, success, general error, offline cached/stale, 401, 403, validation, conflict and retry. Record executed commands and HTML/JUnit reports for rubric evidence.
