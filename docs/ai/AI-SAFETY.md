# AI safety and human-control policy

Status: **[TARGET PRODUCTION DESIGN]**.

## Non-negotiable boundaries

The system never autonomously diagnoses, prescribes, finalizes a record, changes medical facts, overrides the clinician, crosses patient/tenant boundaries or presents provider output as saved. Only an authorized clinician may apply/edit and then explicitly invoke the normal note write.

| Failure mode | Prevention | Detection/response | Human control |
|---|---|---|---|
| prompt injection | fixed instruction hierarchy; context delimiters; no tools instructed by note text | injection evaluation, schema/grounding rejection | clinician sees/refuses draft |
| unsafe diagnosis/prescription request | intended-use policy and forbidden-output checks | refusal/warning; no persistence | manual clinical workflow |
| insufficient/ambiguous context | required-context checks and uncertainty instruction | missingInformation/warnings | clinician supplies information or drafts manually |
| hallucinated fact | grounding source allowlist and unsupported-claim evaluation | reject/flag candidate; monitor rate | verify/edit/reject |
| cross-patient/tenant request | server authorization and context lookup by scoped encounter | fail before provider; security event | none; request denied |
| provider failure/rate limit | timeout/circuit breaker/no client secret | typed unavailable state; manual path | continue note manually |
| model refusal | preserve refusal reason safely | no repeated coercion loop | manual path |
| malformed structure | strict parser/schema; optional evaluated repair once | reject and record validation failure | raw output never rendered/saved |
| stale/terminal encounter | re-check status/version before apply/save | 409 conflict and discard/reconcile | clinician reloads |
| automation bias | persistent AI Draft label, provenance, missing/warning panel | acceptance/edit/reject metrics | explicit confirmation and editable text |

Safety checks run after provider output but cannot replace context authorization. Prompt/output bodies are excluded from logs/analytics by default. A server feature flag disables AI without disabling manual notes. A safety incident freezes affected prompt/model, preserves minimal audit evidence, assesses exposure and follows the approved incident policy.

Evaluation includes Vietnamese and English, direct/indirect/encoded injection, missing and long context, provider/model changes and clinician rejection. Deployment is blocked if safety thresholds are unmet; exact thresholds require clinical/product approval before execution.

## Related documents

[Security threats SEC-T17–T21](../06-SECURITY.md), [Prompt design](AI-PROMPT-DESIGN.md), [Evaluation](AI-EVALUATION.md), [Compliance](../12-COMPLIANCE.md).
