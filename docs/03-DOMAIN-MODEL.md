# Domain model

## Implemented model

```mermaid
erDiagram
  ORGANIZATION ||--o{ FACILITY : contains
  IAM_USER ||--o{ MEMBERSHIP : has
  ORGANIZATION ||--o{ MEMBERSHIP : scopes
  MEMBERSHIP ||--o{ FACILITY_ASSIGNMENT : grants
  FACILITY ||--o{ FACILITY_ASSIGNMENT : assigned
  ORGANIZATION ||--o{ PATIENT : owns
  ORGANIZATION ||--o{ PRACTITIONER : owns
  FACILITY ||--o{ APPOINTMENT : hosts
  PATIENT ||--o{ APPOINTMENT : books
  PRACTITIONER ||--o{ APPOINTMENT : attends
  APPOINTMENT ||--o| QUEUE_ENTRY : checks_in
  QUEUE_ENTRY ||--o| ENCOUNTER : starts
  ENCOUNTER ||--o| CLINICAL_NOTE : documents
```

**[IMPLEMENTED]** Appointment states are `SCHEDULED`, `CANCELLED`, `COMPLETED`, `NO_SHOW`. Queue states are `WAITING`, `CALLED`, `SERVING`, `COMPLETED`, `CANCELLED`. Encounter states are `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.

The encounter invariant is exact: queue entry must be `SERVING` and appointment must remain `SCHEDULED`; patient and practitioner are derived on the server; database constraints allow at most one encounter per appointment and queue entry. Completing an encounter does **not** mutate appointment or queue. The operational workflow therefore requires separate authorized completion actions.

## Target clinical extension

```mermaid
flowchart TD
  P[Patient] --> A[Appointment]
  A --> Q[QueueEntry]
  Q --> E[Encounter]
  E --> N[ClinicalNote - implemented]
  E --> D[Diagnosis - planned]
  E --> O[Observation - planned]
  E --> Rx[Prescription - planned]
  E --> L[LabOrder - planned]
  E --> I[ImagingOrder - planned]
  P --> C[Consent - planned]
  P --> B[Billing - planned]
```

Each future aggregate gets its own lifecycle, permission codes, tenant-safe constraints, audit semantics, and API contract. Narrative AI output is never itself a diagnosis or signed record. Consent and audit schemas created by V1 are empty namespaces, not implemented features.
