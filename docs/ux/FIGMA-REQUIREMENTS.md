# Figma requirements

No Figma file or prototype was found: **[PLANNED / RUBRIC GAP]**.

Create pages for foundations, components, staff-mobile flows, prototypes and handoff. Required frames at a representative Android phone size: splash/session, login, scope selection, dashboard, appointment list/detail/create, queue, patient detail, encounter, clinical note, AI assistant review, profile/logout, permission denied and offline. For each asynchronous screen include loading, empty, error and success; add validation, 401/session-expired and 409/conflict variants where relevant.

Build tokenized light theme first; dark mode only if it will be implemented and tested. Components include all items in `MOBILE-DESIGN-SYSTEM.md` with variants and accessible annotations. Prototype the complete reception→encounter→AI review journey plus back, cancel, error and logout paths.

## Frame-to-code evidence checklist

| Flow/frame | Figma | Compose | Screenshot | Status |
|---|---|---|---|---|
| Login/scope/dashboard | required | required | required | NOT CREATED |
| Appointment list/detail/create | required | required | required | NOT CREATED |
| Queue/encounter/note | required | required | required | NOT CREATED |
| AI generate/review/reject | required | required | required | NOT CREATED |
| Loading/empty/error/success | required | required | required | NOT CREATED |
| Permission/offline/conflict | required | required | required | NOT CREATED |

Handoff includes token values, component behavior, spacing, typography, semantics, copy, validation and navigation links—not screenshots alone.
