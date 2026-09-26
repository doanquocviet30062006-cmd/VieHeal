# ADR-006: Backend AI gateway with human review

## Status

Current architecture decision record. **Proposed [TARGET PRODUCTION DESIGN]**; no AI implementation exists.

## Context

The final project needs one useful real-model use case while clinical data, provider secrets and unsafe output require a server enforcement point.

## Problem

Mobile must not call a model provider directly or allow generated text to become a medical record without validation and clinician control.

## Decision

Route authenticated requests through a backend AI gateway that authorizes encounter context, minimizes it, builds a versioned prompt, invokes a provider adapter, validates schema/safety and returns a draft. A clinician edits/accepts explicitly through permitted application writes.

## Decision Drivers

Secret custody, tenant isolation, prompt/model versioning, provider portability, evaluation and human accountability.

## Alternatives Considered

Direct Android provider call; fixed/fake response; autonomous note save; separate AI microservice immediately; no AI.

## Reasons for Rejection / Trade-offs

Direct mobile calls expose secrets/context control. Fake responses fail the rubric. Autonomous writes are unsafe. A separate service adds premature operations; omitting AI fails product/course goals.

## Consequences

### Positive consequences

Centralized authorization, safety, telemetry, provider switching and kill switch.

### Negative consequences

Latency, cost, provider/legal dependency and continuous evaluation burden.

## Risks

Prompt injection, hallucination, cross-tenant context, provider retention/outage and automation bias.

## Operational Impact

Secret/config management, model/prompt rollout, quotas, circuit breaker, latency/error dashboards and feature flag.

## Security Impact

Context is server-loaded after authorization; no provider key/mobile prompt logs; third-party processing needs legal review.

## Testing Impact

Schema/unit tests, provider adapter tests, authorization tests and at least 15 versioned safety/effectiveness scenarios.

## Revisit Conditions

Independent scaling/compliance/ownership justifies service extraction, or evaluation shows unacceptable risk/value.

## Related Documents

[AI architecture](../ai/AI-ARCHITECTURE.md), [AI safety](../ai/AI-SAFETY.md), [Evaluation](../ai/AI-EVALUATION.md), [Security](../06-SECURITY.md).
