# User flows

```mermaid
flowchart TD
  L[Login] --> S[Select scope]
  S --> A[Appointments]
  A -->|create/update| AP[Persisted appointment]
  AP --> Q[Check in]
  Q --> C[Call]
  C --> SV[Start serving]
  SV --> E[Start encounter]
  E --> N[Edit clinical note]
  N -->|optional| AI[Generate AI draft]
  AI --> R{Clinician review}
  R -->|edit/accept| N
  R -->|reject| N
  N --> EC[Complete encounter]
  EC --> QC[Complete queue separately]
  QC --> AC[Complete appointment separately]
```

Alternate edges: unauthenticated→login; forbidden→permission state; not found→list refresh; conflict→preserve input and reload; offline→cached read/manual retry. Final prototype must exercise these branches, not only the happy path.
