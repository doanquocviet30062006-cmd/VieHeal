# Mobile product scope

No Android project, manifest, Gradle module, Activity, Compose screen, ViewModel, API client, Room database, DataStore or Android test was found. Every item here is **[TARGET PRODUCTION DESIGN]** unless explicitly tied to the implemented backend.

The first release is a staff application for Android. It must demonstrate real OIDC login, scope selection, appointment CRUD-style operations, queue workflow, encounter/note workflow, permission denial, network/error states and one real AI documentation-assistance flow. Patient self-service is deferred to avoid mixing trust models and navigation.

| Epic | MVP | Completion evidence |
|---|---|---|
| Authentication | login/logout/session expiry | PKCE flow against Keycloak, no embedded credentials |
| Context | organization/facility selection | effective permissions and cache isolation |
| Appointments | list/detail/create/reschedule/reason/cancel | persisted API changes and state screenshots |
| Reception | check-in, call, serve, complete/cancel | real queue transitions/conflicts |
| Encounters | start, read, edit note, complete/cancel | server-derived identities and conflict handling |
| AI assistant | draft/summarize note for clinician review | provider call through backend, evaluation evidence |
| Profile | identity, active scope, logout | token/cache cleanup |

Dashboard, patients and profile support the journeys. Practitioner/service/schedule management can remain read-only or outside the demo. Diagnosis, observation, prescriptions, lab/imaging and billing are planned future features.

## Quality gate

Each feature requires UI, navigation, ViewModel, saved/restorable state, real repository, authentication, permission logic, Loading/Empty/Error/Success, validation, automated tests, no production fake dependency, and no secret. The final demo uses synthetic records persisted by PostgreSQL.
