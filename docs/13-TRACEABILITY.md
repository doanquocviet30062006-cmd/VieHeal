Engineering traceability matrix
Status labels refer to implementation, not documentation maturity. Paths are repository-relative and name only verified sources.

ID	Requirement	Status	Domain / architecture	Source code	API / database	Permission	Test	Android feature/screen	Rubric / evidence / gap
FR-AUTH-001	Validate bearer JWT	[IMPLEMENTED]	security resource server	infrastructure/security/SecurityConfiguration.kt	all protected API; IAM identity	authentication prerequisite	integration tests use security test JWTs	auth/session	2.3/2.4; issuer/signature/expiry enforced, audience hardening remains
FR-AUTH-002	Resolve subject to active IAM context	[IMPLEMENTED]	IAM/access context	CurrentAccessContextResolver.kt, AuthMeController.kt, AuthMeResponse.kt	GET /api/v1/auth/me; IAM tables	active system/membership/role/permission/facility	OrganizationApiIntegrationTest	scope selection/profile	2.3/2.4; typed minimal context
FR-AUTH-003	Android Authorization Code + PKCE	[IMPLEMENTED]	mobile security	AppAuthOidcClient.kt, AuthSessionManager.kt, AndroidKeystoreTokenStore.kt	Keycloak discovery/authorization/token/end-session; no backend table	public client + backend application gate	AuthSessionManagerTest	Login	2.3/2.4; live environment browser smoke still required
FR-ORG-001	Create organization	[IMPLEMENTED]	organization + system IAM authorization	OrganizationController, CreateOrganizationUseCase, AccessAuthorizationService, CurrentAccessContextResolver	POST organizations; organizations table; iam.user_system_roles	authenticated system-scoped organization.manage	OrganizationApiIntegrationTest: 401/403/403/201 and persistence assertions	admin future	2.4; P0 authorization hardening implemented
FR-FAC-001	Manage/read facilities	[IMPLEMENTED]	organization/IAM	FacilityController and use cases	facility endpoints; facilities table	facility.manage/read	integration coverage in module flows	scope selection	2.4; direct mobile feature optional
FR-IAM-001	Provision users/memberships/roles/facilities	[IMPLEMENTED]	IAM	IAM controllers/use cases	nine IAM endpoints; seven IAM tables	iam.user.*, iam.role.manage	exercised indirectly/directly in integration setup	admin future/profile context	2.4; dedicated evidence needed
FR-PAT-001	Create/list/get/update patient	[IMPLEMENTED]	patient aggregate	patient controller/use cases/repository	four patient endpoints; patient.patients	patient.create/read/update	PatientApiIntegrationTest	Patient List/Detail	2.3/2.4/2.6; mobile missing
FR-PAT-002	Keep patient in organization/managing facility	[IMPLEMENTED]	tenant-safe persistence	patient use cases/JPA	composite patient→facility FK	same as operation	patient cross-scope/DB tests	Patient Detail	2.4; mobile scope handling missing
FR-PRC-001	Manage practitioner profile	[IMPLEMENTED]	practitioner	practitioner controller/use cases	four endpoints; practitioners table	practitioner.*	PractitionerApiIntegrationTest	supporting selectors/profile	2.4; mobile missing
FR-SVC-001	Manage service catalog and facility offering	[IMPLEMENTED]	service catalog	two service controllers/use cases	eight endpoints; services/facility_services	service.*	ServiceCatalogApiIntegrationTest	appointment selectors	2.4; mobile read integration missing
FR-SCH-001	Configure facility time zone	[IMPLEMENTED]	scheduling	settings controller/use cases	three endpoints; scheduling_settings	schedule.*	SchedulingApiIntegrationTest	supporting admin	2.4; UI optional
FR-SCH-002	Manage availability rules/exceptions	[IMPLEMENTED]	scheduling	availability controllers/use cases	eight endpoints; rules/exceptions	schedule.*	scheduling tests	appointment availability	2.4; mobile UI optional
FR-APT-001	Create persisted appointment	[IMPLEMENTED backend]	appointment orchestration	CreateAppointmentUseCase, controller	POST appointments; appointments table	appointment.create	AppointmentApiIntegrationTest	Create Appointment	2.3/2.4; mobile missing
FR-APT-002	Prevent invalid/overlapping booking	[IMPLEMENTED]	domain/use case/DB	appointment domain/create/reschedule	400/409 contract; GiST exclusions	operation permission	appointment conflict/DB tests	Create/Detail conflict state	2.4/2.6; UI evidence missing
FR-APT-003	List/get/update appointment lifecycle	[IMPLEMENTED backend]	appointment	remaining appointment use cases	seven endpoints; appointment status/checks	appointment.read/update	appointment tests	Appointment List/Detail	2.3/2.4; mobile missing
FR-QUE-001	Check in scheduled appointment	[IMPLEMENTED backend]	reception	CheckInAppointmentUseCase	POST queue; queue_entries	queue.create	QueueApiIntegrationTest	Reception Queue	2.3/2.4; mobile missing
FR-QUE-002	Progress queue lifecycle	[IMPLEMENTED backend]	reception state machine	call/start/complete/cancel use cases	PUT queue actions; lifecycle checks	queue.update	queue lifecycle tests	Reception Queue	2.3/2.4/2.6; UI states missing
FR-QUE-003	Filter/read queue	[IMPLEMENTED backend]	reception query	list/get use cases	GET queue with optional status	queue.read	queue list/filter tests	Reception Queue	2.3/2.4; mobile missing
FR-ENC-001	Start encounter from SERVING queue + SCHEDULED appointment	[IMPLEMENTED backend]	encounter orchestration	StartEncounterUseCase	POST encounters; encounter + note insert	encounter.create	EncounterApiIntegrationTest	Encounter	2.4/2.6; mobile missing
FR-ENC-002	Derive patient/practitioner and prohibit duplicates	[IMPLEMENTED]	server/domain/DB	start use case/repositories	V24 composite FKs and unique keys	encounter.create	encounter DB/integration tests	Encounter	2.4; evidence available
FR-ENC-003	Complete/cancel only IN_PROGRESS	[IMPLEMENTED backend]	encounter state machine	encounter domain, complete/cancel use cases	PUT actions; invalid terminal transition HTTP 400	encounter.update	repeated transition tests	Encounter	2.3/2.4; UI 400 mapping missing
FR-ENC-004	Do not cascade encounter completion	[IMPLEMENTED]	aggregate boundaries	complete encounter use case	queue/appointment unchanged	encounter.update	encounter tests verify boundary	Encounter/Queue/Appointment	2.4; demo must show separate actions
FR-NOTE-001	Read/update one clinical note	[IMPLEMENTED backend]	encounter note	get/update note use cases	GET/PUT note; clinical_notes unique	clinical_note.read/update	encounter tests	Clinical Note	2.3/2.4; mobile editor missing
FR-NOTE-002	Prevent terminal note changes	[IMPLEMENTED]	clinical invariant	UpdateClinicalNoteUseCase	HTTP 409 ENCOUNTER_CLINICAL_NOTE_IMMUTABLE	clinical_note.update still required	encounter immutable test	Clinical Note conflict	2.4/2.6; UI evidence missing
MOB-STATE-001	Deterministic Loading/Empty/Error/Success and refresh/offline/conflict	[PARTIAL FOUNDATION]	ViewModel/UDF	mobile app startup state + core UI primitives	transport error contracts only	UI does not enforce auth	startup JVM + auth-boundary Compose test	app root/auth shell	2.2/2.3/2.6; clinical states remain target
MOB-DATA-001	Scoped minimized Room cache	[TARGET PRODUCTION DESIGN]	repository/Room	TARGET DESIGN	PostgreSQL remains truth; Room local schema	per authenticated scope	TARGET DAO/migration/logout tests	lists/details	2.3/2.4; Room absent
MOB-OFF-001	Visible stale reads; no blind clinical write retry	[TARGET PRODUCTION DESIGN]	repository/WorkManager	TARGET DESIGN	GET refresh only by default	session/scope prerequisite	TARGET offline/conflict tests	banner and forms	2.3/2.8; absent
MOB-SEC-001	No secrets/PHI leakage in APK, logs or analytics	[PARTIAL FOUNDATION]	mobile security/build	manifest + security/network contracts	no API/provider client	n/a	lint/build pass; APK/log/crash inspection pending	entire app	2.3/2.5; foundation only
AI-DOC-001	Draft clinician documentation using authorized encounter context	[TARGET PRODUCTION DESIGN]	backend AI gateway	TARGET DESIGN	planned AI endpoint; no DB contract yet	encounter/note permissions	AI-01…AI-15 NOT EXECUTED	AI Assistant	2.5; no AI code/provider
AI-SAFE-001	No autonomous diagnosis/prescription/finalization	[TARGET PRODUCTION DESIGN]	prompt/safety/human review	TARGET DESIGN	validation before permitted note write	clinical write permission remains required	AI-04/05/15	AI review	2.5/2.6; not implemented
AI-TEN-001	No cross-patient/tenant AI context	[TARGET PRODUCTION DESIGN]	authorization/context builder	TARGET DESIGN	server-loaded encounter context only	encounter/note scope	AI-07/08	AI Assistant	2.5; not implemented
NFR-SEC-001	Least-privilege tenant authorization	[IMPLEMENTED/PARTIAL]	Spring security/IAM	security/access classes	API + IAM tables	complete matrix	module integration tests	role-aware navigation target	2.4; facility consistency/P0 debt
NFR-SEC-002	Health-data-safe mobile storage	[TARGET PRODUCTION DESIGN]	Keystore/Room policy	TARGET DESIGN	local only	user/org/facility partition	TARGET extraction/cleanup tests	all cached screens	2.3; absent
NFR-PERF-001	Measure startup, screen, DB/network and AI latency	[NOT VERIFIED]	performance/observability	no measurement code	target metrics only	n/a	benchmark plan	all screens	2.8; NOT YET MEASURED
NFR-OBS-001	Correlation/log/metric/trace without PHI	[TARGET PRODUCTION DESIGN]	observability	Actuator health only current	target telemetry pipeline	telemetry access policy	telemetry contract tests planned	correlation-aware errors	2.8; mostly absent
NFR-DEP-001	Reproducible staging/production deployment	[PLANNED]	CI/CD/runtime	Dockerfile + dev Compose only	Flyway/PostgreSQL/Keycloak	deploy identities	staging smoke/rollback planned	signed Android build	1B/2.8; not deployed
EVD-SUR-001	Real user survey and analysis	[EVIDENCE GAP]	UX research	none	n/a	n/a	questionnaire only	informs all screens	2.1; unexecuted
EVD-FIG-001	Clickable Figma matching implementation	[EVIDENCE GAP]	design system/prototype	none	n/a	n/a	usability test planned	core frames	2.2; no Figma file
EVD-GIT-001	Meaningful history and review workflow	[IMPLEMENTED/PARTIAL]	project management	seven verified commits	n/a	n/a	history audit	n/a	2.7; no verified issues/PR/review
Maintenance rule
Update a row in the same change that modifies its requirement, permission, endpoint, migration, mobile route, AI prompt or evidence. A target row becomes implemented only when source and proportionate tests exist. Screenshots and demo timestamps are tracked in the rubric evidence matrix, not inferred here.

Related documents
Requirements
API contract
Database
Testing
Rubric evidence
