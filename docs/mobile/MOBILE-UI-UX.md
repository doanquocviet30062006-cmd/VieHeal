# Core mobile screen contracts

Status: every screen is **[TARGET PRODUCTION DESIGN]**; no Compose source or screenshot exists. Role lists reflect current permission mappings, but the runtime permission set from `/auth/me` controls actions.

## Common UI rules

Each asynchronous route implements Default/Loading/Empty/Error/Success where applicable, plus Offline/Stale, 401 SessionExpired, 403 Forbidden, validation and 409 Conflict. All screens use synthetic data for evidence, 48dp targets, scalable text, TalkBack semantics, visible organization/facility scope and non-color status cues. Analytics may record route, action name, duration, outcome and correlation ID—never patient identity or clinical text. Backend audit is required for clinical/administrative writes; mobile analytics is not audit.

## Screen contracts

### SCR-AUTH-001 — Login

- Purpose/roles/route: authenticate any provisioned staff user; `auth/login`; no arguments.
- Sources/ViewModel: OIDC client and session repository; `LoginViewModel`; no VieHeal data before token.
- State/events/actions: Idle, LaunchingBrowser, Authenticating, Error; SignIn, BrowserResult, Retry. Primary sign-in; secondary help/privacy.
- Backend/permission: Keycloak authorization/token then `GET /api/v1/auth/me`; no feature permission.
- Loading/empty/error/403/409/offline: progress during browser/token/context; empty n/a; cancel/network/config errors are actionable; inactive/unresolvable access is denied; 409 n/a; offline cannot establish a new session.
- Validation/success/navigation: validate state/nonce/redirect; success routes scope selection/dashboard and clears login history.
- Acceptance/evidence: Authorization Code + PKCE, no secret/password in APK, cancel/retry/accessibility tests; rubric 2.3/2.4 screenshot/video.

### SCR-DASH-001 — Dashboard

- Purpose/roles/route: entry for active authenticated membership; `dashboard`; active organization/facility IDs from session scope.
- Sources/ViewModel: access context plus appointment/queue summary repositories; `DashboardViewModel`.
- State/events/actions: loading/content/empty/error/stale/forbidden-module; Refresh, OpenAppointments, OpenQueue, OpenProfile, ChangeScope.
- Backend/permission: `/auth/me`; summary calls use `appointment.read`, `queue.read`; cards appear only for effective permissions.
- Recovery: preserve cached summaries on refresh failure; 401 login; 403 hides only affected card; conflicts n/a; offline shows timestamp.
- Success/navigation: typed destinations; no patient details in analytics. Acceptance: role-aware cards, scope visible, all state screenshots; rubric 2.2/2.3.

### SCR-PAT-001 — Patient List

- Purpose/roles/route: find scoped patients; ORGANIZATION_ADMIN/DOCTOR/NURSE/RECEPTIONIST; `patients?facilityId={id}`.
- Source/ViewModel: patient repository/cache; `PatientListViewModel`; GET facility patients; `patient.read`.
- State/events/actions: filter/search, Refresh, Retry, OpenPatient, CreatePatient if `patient.create`; primary select/search; secondary create.
- States: skeleton; no-result/empty action; safe error; 401 login; 403 denied; 409 n/a for read; offline minimized stale summaries.
- Validation/success/navigation: debounce local/server search when added; IDs only to detail/create. Analytics excludes query text. Acceptance: scope/permission, stale label and state tests; rubric 2.3/2.4.

### SCR-PAT-002 — Patient Detail

- Purpose/roles/route: review minimum profile; same read roles; `patients/{patientId}` with UUID.
- Source/ViewModel: GET patient + cache; `PatientDetailViewModel`; `patient.read`; update action only ORGANIZATION_ADMIN/DOCTOR/NURSE.
- Events/actions: Load, Refresh, Edit, Back. Primary workflow-context action; secondary edit where permitted.
- States: detail skeleton; 404 returns list; 401/403 protected; 409 on future edit retains form; offline read only and stale.
- Validation/success/navigation: display full identifiers only where operationally necessary; route to appointments/queue with IDs. No PHI analytics. Acceptance: authorization, masking/minimization, accessibility; rubric 2.2/2.3.

### SCR-APT-001 — Appointment List

- Purpose/roles/route: browse scoped bookings; admin/manager/receptionist/doctor/nurse read; `appointments?facilityId={id}`.
- Sources/ViewModel: appointment repository/Room; `AppointmentListViewModel`; GET appointments; `appointment.read`.
- Events/actions: Load/Refresh/Filter/Open/Create; create visible to admin/manager/receptionist.
- States: state model in `MOBILE-STATE-MANAGEMENT`; empty offers permitted create; 401/403; read has no 409; offline stale/read-only.
- Success/navigation/audit: list sorted/grouped client-side until API query/pagination exists; no PHI analytics. Acceptance: all states, filter restoration and process-death test; rubric 2.3/2.4/2.6.

### SCR-APT-002 — Appointment Detail

- Purpose/roles/route: inspect/manage one booking; read roles; `appointments/{appointmentId}`.
- Sources/ViewModel: GET appointment; related patient/practitioner/service summaries as permitted; `AppointmentDetailViewModel`.
- Events/actions: Reschedule, EditReason, Cancel, Complete, NoShow, CheckIn; writes only admin/manager/receptionist with `appointment.update`; check-in requires `queue.create`.
- States: loading/detail/404/error/stale; 401/403; 400 invalid status; 409 scheduling conflict preserves edit and reload option; offline disables writes.
- Success/navigation: refresh after authoritative mutation; navigate queue after check-in. Clinical/operational actions need backend audit. Acceptance: terminal actions confirmed; conflict/state tests; rubric 2.3/2.4.

### SCR-APT-003 — Create Appointment

- Purpose/roles/route: create booking; admin/manager/receptionist; `appointments/create?facilityId={id}`.
- Sources/ViewModel: patient/practitioner/facility-service reads plus POST appointment; `CreateAppointmentViewModel`; `appointment.create`.
- Events/actions: select patient/practitioner/service, change time/reason, Submit, Cancel; primary Save.
- States: reference-data loading/empty/error; submitting; 401/403; field 400; 409 inactive/unassigned/conflict/unavailable; offline creation disabled.
- Validation/success/navigation: UUID selections, future/valid time business feedback, reason ≤1000; on 201 navigate detail and invalidate list. Analytics records outcome/code only. Acceptance: no blind retry, double-submit prevention and DB persistence proof; rubric 2.3/2.4.

### SCR-QUE-001 — Reception Queue

- Purpose/roles/route: manage current facility queue; admin/manager/receptionist/doctor/nurse; `queue?facilityId={id}`.
- Source/ViewModel: GET queue optional status, queue actions; `QueueViewModel`; `queue.read`, mutations by effective create/update.
- Events/actions: Refresh/Filter/OpenAppointment/Call/StartServing/Complete/Cancel/EditNote/StartEncounter. Primary action depends on state and permission.
- States: ordered loading/content/empty/error/stale; 401/403; 400 invalid lifecycle; 409 check-in/precondition conflicts; offline read only.
- Success/navigation: server response replaces row; start encounter visible only with `encounter.create` and SERVING; no completion cascade. Audit operational transitions. Acceptance: exact state machine and conflict/access tests; rubric 2.3/2.4.

### SCR-ENC-001 — Encounter

- Purpose/roles/route: start/read/complete/cancel clinical session; doctor starts/updates, admin/manager/nurse read; `encounters/{encounterId}` or `encounters/start?queueEntryId={id}`.
- Source/ViewModel: POST/GET/PUT encounter; `EncounterViewModel`; encounter permissions.
- Events/actions: Start, Refresh, OpenNote, Complete, Cancel, Back. Only doctor gets write actions.
- States: start/load/content/error; 401/403/404; 409 start prerequisites/duplicate; repeated terminal complete/cancel is current **HTTP 400**, not 409; offline writes disabled.
- Validation/success/navigation: cancellation reason required ≤500; server-derived patient/practitioner displayed read-only; completion leaves queue/appointment unchanged and offers separate navigation. Clinical audit required. Acceptance: exact invariant/status mapping tests; rubric 2.3/2.4.

### SCR-NOTE-001 — Clinical Note

- Purpose/roles/route: read SOAP note; doctor edits, nurse reads; `encounters/{encounterId}/clinical-note`.
- Source/ViewModel: note GET/PUT and optional protected draft store; `ClinicalNoteViewModel`; clinical_note.read/update.
- Events/actions: EditSection, Save, GenerateAiDraft, Discard, Back. AI visible only to authorized doctor while encounter IN_PROGRESS.
- States: loading/content/error/unsaved/submitting; 401/403/404; field/domain 400; terminal update **409 `ENCOUNTER_CLINICAL_NOTE_IMMUTABLE`**; offline read cache is off by default and unsent draft policy requires approval.
- Validation/success/navigation: lengths 2000/20000; whitespace-only invalid; warn on unsaved exit; save only after 200. Never log note. Acceptance: process-death policy, immutable conflict and accessibility tests; rubric 2.3/2.4.

### SCR-AI-001 — AI Assistant

- Purpose/roles/route: produce reviewable documentation draft for doctor; `encounters/{encounterId}/assistant`.
- Sources/ViewModel: planned backend AI gateway only; `AssistantViewModel`; encounter/note authorization plus future AI permission policy.
- Events/actions: Generate, Cancel, EditDraft, ApplyToEditor, Reject, Retry. Primary generate/review; no direct record save.
- States: instructions/consent, generating, structured draft, refusal, insufficient context, provider failure, invalid output; 401/403/404; conflict if encounter becomes terminal; offline unavailable.
- Validation/success/navigation: show grounding/warnings/model limitation; apply only to local note editor, then clinician saves separately. AI telemetry excludes text. Acceptance: real provider through backend, no key/fake output, AI-01…15 executed; rubric 2.5/2.6.

### SCR-PRO-001 — Profile / Logout

- Purpose/roles/route: show identity/effective scopes and terminate/switch context; all authenticated users; `profile`.
- Source/ViewModel: `/auth/me`, auth/scope/cache repositories; `ProfileViewModel`.
- Events/actions: RefreshContext, ChangeScope, Logout, ConfirmLogout. Primary logout/change scope.
- States: loading/content/error; 401 routes login; 403 context error; 409 n/a; offline displays last known identity but scope change may require validation.
- Success/navigation/security: logout revokes where supported, erases tokens/cache/drafts, clears back stack regardless of network logout outcome. Analytics records logout outcome only. Acceptance: multi-user/scope residue test; rubric 2.3/2.4.

## Related documents

[Navigation](MOBILE-NAVIGATION.md), [State management](MOBILE-STATE-MANAGEMENT.md), [Design system](MOBILE-DESIGN-SYSTEM.md), [Figma requirements](../ux/FIGMA-REQUIREMENTS.md), [Accessibility](../ux/ACCESSIBILITY.md).
