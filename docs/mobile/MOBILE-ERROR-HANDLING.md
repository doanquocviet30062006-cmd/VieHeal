# Mobile error handling

Error handling is a typed pipeline: transport/HTTP/ProblemDetail → domain error → feature-specific message and recovery action. Preserve backend `code` and `currentState` from 409 responses while hiding internal exception detail.

| Error | Recovery |
|---|---|
| Validation | map stable field keys; keep input; focus first invalid field |
| Offline/timeout | show cache and safe retry; do not duplicate writes |
| Session expired | one refresh; login with draft-preservation policy |
| Forbidden | remove disallowed action and offer safe navigation |
| Not found | invalidate cache, return to list with notice |
| Conflict | retain user draft, display current state, reload/merge explicitly |
| Server/protocol | generic safe message, correlation ID, retry later |

Loading disables duplicate submissions but retains cancel/back when safe. A success message occurs only after authoritative response, not optimistic animation. Never show stack traces, SQL, token details or provider errors. Accessibility services announce validation and global errors.

Unknown errors are reported through scrubbed telemetry. Global handlers manage session state and connectivity banners; business errors remain in their feature ViewModel. Tests verify every status mapping and ensure coroutine cancellation produces no user-visible failure.
