# Project overview

## Problem and users

VieHeal addresses fragmented clinic workflows: staff often re-enter patient, booking, queue, and encounter information across disconnected tools, while clinicians need timely context without allowing automation to make medical decisions. Target users are receptionists, clinicians, nurses, clinic managers, administrators, and—later—patients. This is a proposed problem framing; **EVIDENCE GAP – REAL USER SURVEY REQUIRED** before it can be presented as validated.

The MVP boundary is a staff-facing Android app connected to a real VieHeal backend for authentication, patients, appointments, reception queue, encounter notes, and a clinician-controlled documentation assistant. Diagnosis, prescribing, orders, billing, and a patient self-service app are outside the first demonstrable slice.

## Capability status

| Capability | Status | Repository evidence |
|---|---|---|
| Organization/facility and IAM | [IMPLEMENTED] | `modules/organization`, `modules/iam`, V1–V5 |
| Patient and practitioner | [IMPLEMENTED] | corresponding modules, V6–V9 |
| Service catalog and scheduling | [IMPLEMENTED] | corresponding modules, V10–V15 |
| Appointment and reception queue | [IMPLEMENTED] | corresponding modules, V16–V21 |
| Encounter and clinical note | [IMPLEMENTED] | `modules/encounter`, V22–V24 |
| Android client | [PLANNED] | no Android Gradle module or manifest found |
| AI gateway/provider | [PLANNED] | `ai-platform/` is empty |
| Diagnosis through billing | [PLANNED] | no source or tables found |
| Consent/audit behavior | [PLANNED] | schemas exist, but no tables or services |
| Production deployment | [PLANNED] | development Compose only |

## Outcomes and guardrails

Success means that an authorized user can authenticate, see only permitted organization/facility data, perform a persisted workflow, recover from network and process interruptions, and review any AI draft before saving. Patient/practitioner identity must be server-derived for encounters; clients must not override it. Logs, analytics, crash reports, and model prompts must minimize health data.

Out of scope for an autonomous AI: diagnosis, prescription, finalization of records, silent clinical data changes, or overriding a clinician. The course deliverable must show actual application behavior and label missing evidence rather than simulate completion.
