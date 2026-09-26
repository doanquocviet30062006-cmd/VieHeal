# Mobile network contract

Status: **[TARGET PRODUCTION DESIGN]**. `core:network` owns Retrofit/OkHttp, DTOs, serialization and transport error parsing; feature modules see repository interfaces/domain errors only.

## Client stack and interceptors

One configured client per trust domain. VieHeal API client attaches `Authorization: Bearer`, `X-Correlation-ID`, app version and locale. Token refresh is an authenticator/session operation serialized across requests; logging is debug-only, header/body redacted and absent from release. AI provider is never a mobile client.

Initial budgets: connect 10 s, VieHeal read/write 30 s, backend AI response 60 s with user cancellation. These are **[NOT VERIFIED]** and require measured adjustment. Kotlin serialization/Jackson converter choice must match backend ISO-8601 instants, dates, local times, UUIDs and enums; unknown enum handling produces ProtocolError rather than unsafe default.

## Current backend status mapping

| Status/result | Current meaning | Domain/UI behavior |
|---|---|---|
| 400 | malformed/Bean Validation/DomainValidation, including invalid Encounter terminal transition | parse ProblemDetail/code; form/operation validation; retain input |
| 401 | missing/invalid/expired JWT | refresh exactly once if supported; then SessionExpired and protected cleanup |
| 403 | authenticated but permission/scope denied | Forbidden; remain signed in; no refresh loop |
| 404 | scoped resource/relationship absent | NotFound; invalidate item and return/refresh safely |
| 409 | duplicate or ResourceStateConflict, including immutable terminal note | Conflict(code,currentState); preserve draft and reconcile |
| 5xx | unhandled/server dependency failure | ServerUnavailable; correlation ID; bounded safe retry |
| timeout/DNS/no network | uncertain connectivity/outcome | offline/timeout; never claim write failure or success without reconciliation |

HTTP 422 and 429 are **not current backend contract**. If introduced, map 422 to semantic validation and 429 to Retry-After-aware throttling. Pagination is also absent; add a cursor/page contract before lists grow, without inventing query parameters now.

## Retry matrix

| Operation | Automatic retry |
|---|---|
| GET before any response | at most 2 bounded exponential+jitter retries for connectivity/selected 5xx; cancel when route leaves |
| GET 401 | one serialized token refresh and replay, then logout |
| POST/PUT clinical or operational write | **never blindly retry**; outcome may be committed; reconcile with GET |
| future idempotent write | only with server-supported idempotency key/version and documented window |
| 400/403/404/409 | no automatic retry; user/action/state must change |
| 429 target | respect valid Retry-After, cap delay, surface throttled state |

Connectivity callbacks only influence presentation; the request result is authoritative. Correlation ID persists across one logical attempt family but a user-initiated new action gets a new ID. ProblemDetail parser retains status/title/detail/code/currentState/correlation identifier while discarding server internals.

## Contract testing

Use MockWebServer for serialization, headers, timeouts, cancellation and every status; staging smoke tests verify current DTOs. Do not log bodies in tests containing realistic PHI. A contract change updates the [API contract](../04-API-SPEC.md), mapper tests and relevant screen contracts together.

## Related documents

[API contract](../04-API-SPEC.md), [Mobile auth](MOBILE-AUTH.md), [Error handling](MOBILE-ERROR-HANDLING.md), [Security](MOBILE-SECURITY.md).
