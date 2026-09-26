# Healthcare and personal-data engineering controls

This is an engineering control matrix, not a certification. Regulatory applicability and legal sufficiency are **[REQUIRES LEGAL REVIEW]**.

| Control | Reason | Implementation status | Source/evidence | Gap | Required policy/legal review |
|---|---|---|---|---|---|
| health/personal data inventory | know fields, purpose, processors and flow | [PLANNED] | domain/database docs only | no formal register | lawful basis, classification, residency |
| authentication | identify actors | [IMPLEMENTED/PARTIAL] | JWT resource server/dev Keycloak | production realm/mobile PKCE absent | identity assurance/MFA/session policy |
| authorization | least privilege and tenant isolation | [IMPLEMENTED/PARTIAL] | IAM migrations/access service/tests | public org creation; facility consistency | role/access review frequency/break-glass |
| consent | capture purpose/version/withdrawal | [PLANNED] | empty consent schema only | no tables/workflow/enforcement | when/whose consent; minors/proxies |
| audit | accountability/non-repudiation | [PLANNED] | actor columns, empty audit schema | no append-only access/write audit | event scope, access, retention, legal hold |
| retention | avoid indefinite data | [REQUIRES POLICY DECISION] | none | no class schedules/jobs | clinical/legal minimums and holds |
| deletion | fulfill approved lifecycle | [PLANNED] | no delete API | anonymization/deletion/cascade rules absent | legal duty vs medical-record preservation |
| export/access | authorized data portability/access | [PLANNED] | no export service | identity, format, redaction absent | entitlement and response process |
| encryption in transit | prevent interception | [TARGET PRODUCTION DESIGN] | dev HTTP only | TLS/cert operations absent | approved TLS/endpoint boundary |
| encryption at rest/keys | reduce storage exposure | [TARGET PRODUCTION DESIGN] | no production evidence | DB/backup/mobile key policy absent | key custody/rotation/residency |
| backups/recovery | availability/integrity | [TARGET PRODUCTION DESIGN] | Docker volume only | no encrypted backup/restore/RPO/RTO | retention, region, recovery obligations |
| incident response | contain/investigate/notify | [PLANNED] | none | no roles/runbook/evidence handling | notification thresholds/timelines |
| third-party AI | control disclosure and model risk | [REQUIRES LEGAL REVIEW] | target docs; no provider | provider/DPA/region/retention/model use undecided | PHI eligibility, processor terms, human oversight |
| mobile cache | loss/device/scope risk | [TARGET PRODUCTION DESIGN] | cache policy only | Room/cleanup/encryption absent | offline need, screen/backup policy |
| logs/telemetry | operate without secondary disclosure | [IMPLEMENTED/PARTIAL] | basic INFO logs/Actuator | no structured redaction/access/retention | telemetry processor and retention |
| mobile analytics/crash | quality without PHI leakage | [TARGET PRODUCTION DESIGN] | no SDK | vendor/config/scrub tests absent | consent/disclosure/processor choice |
| access review | remove excessive/stale access | [PLANNED] | active statuses exist | no periodic certification/offboarding evidence | review owner/frequency |
| secure development | reduce vulnerabilities | [PARTIAL] | integration tests/Git | CI scans/threat review/pen test absent | release risk acceptance |

Before production, create a record for each data class: owner, fields, purpose, users, source, destinations/processors, legal basis, region, retention, deletion/export, encryption and audit. Separate clinical audit from diagnostic telemetry. Synthetic test data does not remove the need to secure the production design.

## Related documents

[Security](06-SECURITY.md), [Database](05-DATABASE.md), [Mobile data](mobile/MOBILE-DATA.md), [AI safety](ai/AI-SAFETY.md), [Deployment](10-DEPLOYMENT.md).
