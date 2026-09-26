# Primary AI use case: clinician documentation draft

Status: **[TARGET PRODUCTION DESIGN]**. Prefer this one deep use case over unrelated chatbot features.

## Intended workflow

Input is a doctor-initiated request from an authorized IN_PROGRESS Encounter plus existing clinician-authored SOAP sections. Backend loads only this encounter's authorized context, constructs `clinical-note-draft/v1`, invokes the configured model, validates structured output and returns a visibly unverified draft. Clinician reviews sources/warnings, edits or rejects it, applies selected text to the local note editor and explicitly saves through the normal clinical-note operation. The backend audit metadata records the AI version and clinician decision without treating model output as medical truth.

| Stage | Contract |
|---|---|
| input | encounter ID, task, requested sections, locale; bounded and no arbitrary patient ID/context |
| authorized context | current encounter identity/status and explicitly selected clinician-authored note text; minimum necessary demographics only if approved |
| prompt/model | versioned system/task/schema; server-selected provider/model/parameters |
| validation | strict JSON, allowed fields/lengths, grounding, prohibited behavior and encounter still mutable |
| human review | draft label, missing information, warnings and edit/reject/apply controls |
| persistence | only explicit clinician save through existing application authorization; AI never directly finalizes |
| audit | actor/scope/task/versions/outcome/correlation; no routine content logging |

Acceptance requires a real provider via backend, no embedded mobile key or fixed response, cross-tenant denial before provider call, valid Vietnamese handling, manual workflow on failure, and executed evaluation. The assistant must not diagnose, prescribe, finalize, modify medical facts, override a clinician or retrieve another patient/tenant.

Later candidates—encounter summarization, structured extraction and patient-friendly explanation of already approved information—remain **[PLANNED]** and require separate intended-use/safety evaluation.

## Related documents

[AI architecture](AI-ARCHITECTURE.md), [Safety](AI-SAFETY.md), [Mobile screen contract](../mobile/MOBILE-UI-UX.md), [Clinical boundaries](../adr/ADR-007-clinical-workflow-boundaries.md).
