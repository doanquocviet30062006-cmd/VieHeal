# Observability design and signal inventory

Status: Spring Actuator health/info and INFO application logging are **[IMPLEMENTED]**. Structured correlation, metrics, traces, dashboards, alerts and mobile/AI telemetry are **[TARGET PRODUCTION DESIGN]**.

## Correlation and privacy

Android creates a random correlation ID per logical operation; OkHttp sends `X-Correlation-ID`; gateway/backend validates length/format or replaces it; backend includes it in response and ProblemDetail and propagates to DB/AI spans. Never encode user/patient/tenant IDs in the correlation ID. Telemetry allowlists route templates, status, duration, version and outcome. Tokens, raw path IDs, query/search text, request/response bodies, ClinicalNote and AI content are excluded by default.

## Event and metric inventory

| Name | Type / attributes | Trigger / purpose | PHI rule / alert |
|---|---|---|---|
| `http.server.requests` | counter/histogram: route template, method, status, env | every API request; p50/p95/p99/error | no raw path; alert sustained 5xx/latency |
| `security.authentication.failure` | counter: reason class, issuer category | invalid/expired token trend | no token/subject; anomaly alert |
| `security.authorization.denied` | counter: permission code, route, scope type | 403 monitoring | no resource ID; rate anomaly |
| `db.pool` | gauges: active/idle/pending/max | saturation | alert pending/exhaustion |
| `db.query.duration` | histogram: repository/operation allowlist | slow persistence | no SQL values; p95 alert |
| `flyway.state` | event/gauge: expected/current version, success | startup/deploy | alert mismatch/failure |
| `backup.age` / `restore.last_success` | gauge/event | recovery readiness | alert beyond policy |
| `keycloak.dependency` | availability/latency | discovery/JWK/admin-independent health | degraded auth alert; no account data |
| `ai.request` | counter/histogram: provider/model/prompt/schema/task/outcome | usage, latency, failure | no prompt/response/context |
| `ai.validation.failure` | counter: schema/safety/grounding class | detect model regression | threshold blocks rollout |
| `ai.clinician.decision` | counter: accept/edit/reject | utility/safety evaluation | no content/user/patient ID |
| `mobile.crash` | crash-free sessions/build/device class | release health | scrub PHI/tokens/breadcrumbs |
| `mobile.anr` | rate/build/device class | responsiveness | alert release regression |
| `mobile.screen.load` | histogram: screen/build/source/cache freshness/outcome | user-perceived latency | no route object ID |
| `mobile.sync.age` | histogram: resource type/scope category | stale cache risk | no scope ID; alert extreme age |
| `audit.clinical_write` | append-only event: actor ref, scope refs, action, target type/ref, outcome/correlation | accountability | separate controlled audit store; no content copy |

## Logs, metrics and traces

Structured JSON logs include timestamp, severity, service/version/environment, trace/correlation ID, route template, outcome and stable error code. Exception stacks are restricted and scrubbed. Metrics aggregate; avoid unbounded UUID labels. Distributed traces cover gateway→controller→use case→repository and gateway→provider, but span attributes exclude payloads. Sampling keeps security/audit requirements separate from diagnostic traces.

## Health model and dashboards

Separate liveness/readiness/degraded dependency status. Dashboards: API golden signals; DB/migration/backup; authentication/authorization; AI provider/validation/cost; Android crash/ANR/performance; security events. Each alert has severity, threshold, evaluation window, owner, runbook, escalation and recovery condition. Thresholds are **[NOT VERIFIED]** until baselines exist.

## Verification

Contract tests emit synthetic sensitive markers through success/failure and assert absence from logs, traces, crash and analytics exports. Load/failure tests verify metric labels/cardinality and alerts. Access/retention/export policies for telemetry and audit are **[REQUIRES POLICY DECISION]**.

## Related documents

[Deployment](10-DEPLOYMENT.md), [Security](06-SECURITY.md), [Mobile performance](mobile/MOBILE-PERFORMANCE.md), [AI architecture](ai/AI-ARCHITECTURE.md).
