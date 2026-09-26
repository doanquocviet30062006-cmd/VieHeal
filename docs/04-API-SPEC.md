# API contract

Status: **[IMPLEMENTED]** contract reconstructed from the current controllers, DTOs, use cases, domain types and integration tests. Base prefix is `/api/v1`. This document covers 65 explicit controller endpoints: 64 module endpoints and one application health endpoint. Spring Boot also exposes `/actuator/health`; it is framework-managed, not a VieHeal controller operation.

## Protocol and common behavior

JSON uses Kotlin/Jackson property names shown below. UUID and enum conversion failures, malformed JSON and Jakarta Bean Validation failures return HTTP 400. Protected operations require `Authorization: Bearer <JWT>`: missing/invalid/expired authentication returns 401; authenticated callers without the required permission or scope return 403. `ResourceNotFoundException` maps to 404. `ResourceAlreadyExistsException` and `ResourceStateConflictException` map to 409. `DomainValidationException` maps to 400. Error bodies are Spring `ProblemDetail`; domain errors may add `code` and conflicts may add `currentState`.

Unless a row says public, it inherits 401 and 403 behavior. A path containing both `{organizationId}` and `{facilityId}` is repository-filtered by both identifiers, but most controllers call `requireOrganizationPermission`; facility assignment is not uniformly checked at the controller for these routes. This is a verified distinction, not an authorization recommendation.

## Request and response schemas

| DTO | Fields and validation |
|---|---|
| `CreateOrganizationRequest` | `code` required 2–50; `name` required 2–255 |
| `CreateFacilityRequest` | `code` required 2–50; `name` required 2–255; optional address ≤500, ward/district/province ≤150, countryCode length 2 default `VN`, phone ≤30, valid email ≤255 |
| `CreateOrganizationUserRequest` | optional valid email ≤255, phone ≤30; `displayName` required 2–255 |
| `AddOrganizationMemberRequest` | non-null `userId` UUID |
| `AssignMembershipRoleRequest` | `roleCode` required 2–100 |
| `AssignFacilityRequest` | non-null `facilityId` UUID |
| `CreatePatientRequest` | required patientCode ≤50, fullName ≤255, `sex`; optional DOB/contact/address; valid email; countryCode length 2 default `VN` |
| `UpdatePatientRequest` | required fullName ≤255, `sex`, countryCode length 2; optional DOB/contact/address; valid email |
| `CreatePractitionerRequest` | membershipId, practitionerCode ≤50, fullName ≤255, practitionerType; optional license ≤100, specialty ≤150, phone ≤30, valid email |
| `UpdatePractitionerRequest` | fullName ≤255 and practitionerType required; optional license/specialty/phone/email with the same limits |
| `CreateMedicalServiceRequest` | serviceCode ≤50 and name ≤255 required; optional description ≤1000/category ≤100; positive defaultDurationMinutes |
| `UpdateMedicalServiceRequest` | name required ≤255; optional description/category; positive defaultDurationMinutes |
| `Create/UpdateFacilityServiceRequest` | serviceId only on create; positive duration; price ≥0 with 10 integer/2 fraction digits; 3-letter currency; bookingEnabled |
| `UpsertFacilitySchedulingSettingsRequest` | nonblank timeZoneId ≤100; domain also validates a real zone |
| `Create/UpdatePractitionerAvailabilityRuleRequest` | dayOfWeek 1–7, start/end local time, effectiveFrom, optional effectiveTo; domain validates ordering |
| `Create/UpdatePractitionerAvailabilityExceptionRequest` | exceptionType, startAt/endAt instants, optional reason ≤500; domain validates ordering |
| `CreateAppointmentRequest` | patientId, practitionerId, facilityServiceId, scheduledStartAt; optional reason ≤1000 |
| `RescheduleAppointmentRequest` | scheduledStartAt |
| `UpdateAppointmentReasonRequest` | nullable reason ≤1000 |
| `CancelAppointmentRequest` | nonblank cancellationReason ≤500 |
| `CheckInQueueEntryRequest` | appointmentId; optional note ≤1000 |
| `UpdateQueueEntryNoteRequest` | nullable note ≤1000 |
| `StartEncounterRequest` | queueEntryId |
| `UpdateClinicalNoteRequest` | nullable chiefComplaint ≤2000; nullable subjective/objective/assessment/plan each ≤20000; whitespace-only non-null sections fail domain validation |
| `CancelEncounterRequest` | nonblank cancellationReason ≤500 |

Response families expose these important fields: organization/facility identity and status; IAM user/membership/role/assignment identity and status; patient/practitioner identity, contact and status; services, price/duration/booking status; schedule rule/exception/settings values; appointment linkage, time, status and actor timestamps; queue linkage/lifecycle timestamps/note; encounter linkage/lifecycle/actors; and SOAP clinical-note sections/actors/timestamps. List operations return JSON arrays and currently have no pagination.

## Health, organization and facility

| Operation | Method and exact path | Auth / permission / scope | Params / request | Response / success | Errors, preconditions and side effects |
|---|---|---|---|---|---|
| Application health | `GET /api/v1/health` | public | none | `HealthResponse(status,service,timestamp)`, 200 | no dependency readiness check; no mutation |
| Create organization | `POST /api/v1/organizations` | **public; no permission** | `CreateOrganizationRequest` | `OrganizationResponse`, 201 | 400 validation; 409 duplicate code; creates organization. **P0 SECURITY DEBT BEFORE PRODUCTION** |
| Get organization | `GET /api/v1/organizations/{id}` | JWT; `organization.read`; membership in `id` | path `id` UUID | `OrganizationResponse`, 200 | 400 invalid UUID; 401/403; 404; read-only transaction |
| Create facility | `POST /api/v1/organizations/{organizationId}/facilities` | JWT; `facility.manage`; organization scope | path organizationId; `CreateFacilityRequest` | `FacilityResponse`, 201 | 400/401/403/404 organization/409 duplicate code; creates facility |
| List facilities | `GET /api/v1/organizations/{organizationId}/facilities` | JWT; `facility.read`; organization scope plus result filtered to assigned facility IDs | path organizationId | `FacilityResponse[]`, 200 | 400/401/403/404; read-only |
| Get facility | `GET /api/v1/facilities/{facilityId}` | JWT; `facility.read`; active assignment containing facility | path facilityId | `FacilityResponse`, 200 | 400/401/403/404; read-only |

## IAM

| Operation | Method and exact path | Auth / permission / scope | Params / request | Response / success | Errors, preconditions and side effects |
|---|---|---|---|---|---|
| Current access context | `GET /api/v1/auth/me` | JWT; no explicit permission; active internal user/context resolution | none | map containing subject, username, email, IAM identity/status, organizations with membership, roles, permissions and facility IDs, issuer; 200 | 401; 403/resolution failure possible; no mutation |
| Get IAM user | `GET /api/v1/iam/users/{id}` | JWT; `iam.user.read`; caller must share an authorized active organization with target user | path id | `IamUserResponse`, 200 | 400/401/403/404; read-only |
| Provision organization user | `POST /api/v1/organizations/{organizationId}/users` | JWT; `iam.user.manage`; organization scope | path organizationId; `CreateOrganizationUserRequest` | `IamUserResponse`, 201 | 400/401/403/404/409 email or identity conflict; creates user and organization membership in one use case transaction |
| Add existing member | `POST /api/v1/organizations/{organizationId}/members` | JWT; `iam.user.manage`; organization scope | organizationId; `AddOrganizationMemberRequest` | `OrganizationMembershipResponse`, 201 | 400/401/403/404/409 existing membership; creates membership |
| List members | `GET /api/v1/organizations/{organizationId}/members` | JWT; `iam.user.read`; organization scope | organizationId | `OrganizationMembershipResponse[]`, 200 | 400/401/403/404; read-only |
| Assign role | `POST /api/v1/memberships/{membershipId}/roles` | JWT; `iam.role.manage`; target membership's organization scope | membershipId; `AssignMembershipRoleRequest` | `IamRoleResponse`, 201 | 400/401/403/404 role/membership/409 duplicate mapping; creates membership-role |
| List membership roles | `GET /api/v1/memberships/{membershipId}/roles` | JWT; `iam.role.manage`; target membership's organization scope | membershipId | `IamRoleResponse[]`, 200 | 400/401/403/404; read-only |
| Assign facility | `POST /api/v1/memberships/{membershipId}/facilities` | JWT; `iam.user.manage`; target membership's organization scope | membershipId; `AssignFacilityRequest` | `FacilityAssignmentResponse`, 201 | 400/401/403/404/mismatched facility/409 duplicate; creates assignment |
| List membership facilities | `GET /api/v1/memberships/{membershipId}/facilities` | JWT; `iam.user.read`; target membership's organization scope | membershipId | `FacilityAssignmentResponse[]`, 200 | 400/401/403/404; read-only |

`CreateIamUserRequest` exists in source but no current public controller accepts it; user creation exposed to clients is organization provisioning above.

## Patient and practitioner

| Operation | Method and exact path | Auth / permission / scope | Params / request | Response / success | Errors, preconditions and side effects |
|---|---|---|---|---|---|
| Create patient | `POST /api/v1/organizations/{organizationId}/facilities/{facilityId}/patients` | JWT; `patient.create`; organization scope | org/facility IDs; `CreatePatientRequest` | `PatientResponse`, 201 | 400/401/403/404 organization/facility/409 inactive or duplicate patient code; actor stored; patient managing facility fixed from path |
| List patients | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/patients` | JWT; `patient.read`; organization scope | org/facility IDs; no query | `PatientResponse[]`, 200 | 400/401/403/404; read-only, scoped query |
| Get patient | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/patients/{patientId}` | JWT; `patient.read`; organization scope | three UUIDs | `PatientResponse`, 200 | 400/401/403/404; read-only |
| Update patient | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/patients/{patientId}` | JWT; `patient.update`; organization scope | IDs; `UpdatePatientRequest` | `PatientResponse`, 200 | 400/401/403/404/409 inactive organization/facility; patient code and scope remain immutable; actor updated |
| Create practitioner | `POST /api/v1/organizations/{organizationId}/practitioners` | JWT; `practitioner.create`; organization scope | organizationId; `CreatePractitionerRequest` | `PractitionerResponse`, 201 | 400/401/403/404 membership/409 inactive or duplicate code/membership; actor stored |
| List practitioners | `GET /api/v1/organizations/{organizationId}/practitioners` | JWT; `practitioner.read`; organization scope | organizationId | `PractitionerResponse[]`, 200 | 400/401/403/404; read-only |
| Get practitioner | `GET /api/v1/organizations/{organizationId}/practitioners/{practitionerId}` | JWT; `practitioner.read`; organization scope | IDs | `PractitionerResponse`, 200 | 400/401/403/404; read-only |
| Update practitioner | `PUT /api/v1/organizations/{organizationId}/practitioners/{practitionerId}` | JWT; `practitioner.update`; organization scope | IDs; `UpdatePractitionerRequest` | `PractitionerResponse`, 200 | 400/401/403/404/409 inactive membership; code/membership remain immutable; actor updated |

## Service catalog

| Operation | Method and exact path | Auth / permission / scope | Params / request | Response / success | Errors, preconditions and side effects |
|---|---|---|---|---|---|
| Create medical service | `POST /api/v1/organizations/{organizationId}/services` | JWT; `service.create`; organization scope | organizationId; `CreateMedicalServiceRequest` | `MedicalServiceResponse`, 201 | 400/401/403/404/409 inactive organization or duplicate code; creates service |
| List medical services | `GET /api/v1/organizations/{organizationId}/services` | JWT; `service.read`; organization scope | organizationId | `MedicalServiceResponse[]`, 200 | 400/401/403/404; read-only |
| Get medical service | `GET /api/v1/organizations/{organizationId}/services/{serviceId}` | JWT; `service.read`; organization scope | IDs | `MedicalServiceResponse`, 200 | 400/401/403/404; read-only |
| Update medical service | `PUT /api/v1/organizations/{organizationId}/services/{serviceId}` | JWT; `service.update`; organization scope | IDs; `UpdateMedicalServiceRequest` | `MedicalServiceResponse`, 200 | 400/401/403/404/409 inactive organization; service code immutable |
| Configure facility service | `POST /api/v1/organizations/{organizationId}/facilities/{facilityId}/services` | JWT; `service.create`; organization scope | IDs; `CreateFacilityServiceRequest` | `FacilityServiceResponse`, 201 | 400/401/403/404/409 inactive org/facility/service or duplicate mapping; creates price/duration configuration |
| List facility services | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/services` | JWT; `service.read`; organization scope | IDs | `FacilityServiceResponse[]`, 200 | 400/401/403/404; read-only |
| Get facility service | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/services/{facilityServiceId}` | JWT; `service.read`; organization scope | IDs | `FacilityServiceResponse`, 200 | 400/401/403/404; read-only |
| Update facility service | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/services/{facilityServiceId}` | JWT; `service.update`; organization scope | IDs; `UpdateFacilityServiceRequest` | `FacilityServiceResponse`, 200 | 400/401/403/404/409 inactive scope; service/facility identity immutable |

## Scheduling

| Operation | Method and exact path | Auth / permission / scope | Params / request | Response / success | Errors, preconditions and side effects |
|---|---|---|---|---|---|
| Create scheduling settings | `POST /api/v1/organizations/{organizationId}/facilities/{facilityId}/scheduling-settings` | JWT; `schedule.create`; organization scope | IDs; `UpsertFacilitySchedulingSettingsRequest` | `FacilitySchedulingSettingsResponse`, 201 | 400 invalid zone/401/403/404/409 existing settings or inactive scope; creates one settings row per facility |
| Get scheduling settings | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/scheduling-settings` | JWT; `schedule.read`; organization scope | IDs | `FacilitySchedulingSettingsResponse`, 200 | 400/401/403/404; read-only |
| Update scheduling settings | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/scheduling-settings` | JWT; `schedule.update`; organization scope | IDs; `UpsertFacilitySchedulingSettingsRequest` | response, 200 | 400/401/403/404/409; changes time-zone configuration |
| Create availability rule | `POST /api/v1/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-rules` | JWT; `schedule.create`; organization scope | IDs; create rule DTO | `PractitionerAvailabilityRuleResponse`, 201 | 400 range/401/403/404/409 scope or overlap/state; creates rule |
| List availability rules | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-rules` | JWT; `schedule.read`; organization scope | IDs | response array, 200 | 400/401/403/404; read-only |
| Get availability rule | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-rules/{ruleId}` | JWT; `schedule.read`; organization scope | IDs | response, 200 | 400/401/403/404; read-only |
| Update availability rule | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-rules/{ruleId}` | JWT; `schedule.update`; organization scope | IDs; update rule DTO | response, 200 | 400/401/403/404/409; updates schedule rule |
| Create availability exception | `POST /api/v1/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-exceptions` | JWT; `schedule.create`; organization scope | IDs; create exception DTO | `PractitionerAvailabilityExceptionResponse`, 201 | 400 range/401/403/404/409 scope/state; creates exception |
| List availability exceptions | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-exceptions` | JWT; `schedule.read`; organization scope | IDs | response array, 200 | 400/401/403/404; read-only |
| Get availability exception | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-exceptions/{exceptionId}` | JWT; `schedule.read`; organization scope | IDs | response, 200 | 400/401/403/404; read-only |
| Update availability exception | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/practitioners/{practitionerId}/availability-exceptions/{exceptionId}` | JWT; `schedule.update`; organization scope | IDs; update exception DTO | response, 200 | 400/401/403/404/409; updates exception |

## Appointment

| Operation | Method and exact path | Auth / permission / scope | Params / request | Response / success | Errors, preconditions and side effects |
|---|---|---|---|---|---|
| Create appointment | `POST /api/v1/organizations/{organizationId}/facilities/{facilityId}/appointments` | JWT; `appointment.create`; organization scope | IDs; `CreateAppointmentRequest` | `AppointmentResponse`, 201 | 400 time/reason; 401/403; 404 related records; 409 inactive/unassigned/booking-disabled/settings missing/time conflict/unavailable/outside availability; computes end from service duration and writes actors |
| List appointments | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/appointments` | JWT; `appointment.read`; organization scope | IDs; no query/pagination | response array, 200 | 400/401/403/404; read-only |
| Get appointment | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}` | JWT; `appointment.read`; organization scope | IDs | response, 200 | 400/401/403/404; read-only |
| Reschedule | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/reschedule` | JWT; `appointment.update`; organization scope | IDs; `RescheduleAppointmentRequest` | response, 200 | 400 invalid/not scheduled; 401/403/404; 409 same scheduling conflicts as create; changes start/end and actor |
| Update reason | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/reason` | JWT; `appointment.update`; organization scope | IDs; `UpdateAppointmentReasonRequest` | response, 200 | 400 too long/not scheduled; 401/403/404; no distinct 409 currently verified; changes reason/actor |
| Cancel | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/cancel` | JWT; `appointment.update`; organization scope | IDs; `CancelAppointmentRequest` | response, 200 | 400 reason or invalid current status; 401/403/404; terminal state/actor mutation |
| Complete | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/complete` | JWT; `appointment.update`; organization scope | IDs; no body | response, 200 | 400 if not scheduled; 401/403/404; terminal state/actor mutation |
| Mark no-show | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/appointments/{appointmentId}/no-show` | JWT; `appointment.update`; organization scope | IDs; no body | response, 200 | 400 if not scheduled; 401/403/404; terminal state/actor mutation |

## Reception queue

| Operation | Method and exact path | Auth / permission / scope | Params / request | Response / success | Errors, preconditions and side effects |
|---|---|---|---|---|---|
| Check in appointment | `POST /api/v1/organizations/{organizationId}/facilities/{facilityId}/queue` | JWT; `queue.create`; organization scope | IDs; `CheckInQueueEntryRequest` | `QueueEntryResponse`, 201 | 400 note/domain; 401/403/404; 409 appointment not eligible/already queued/scope state; derives patient/practitioner and creates `WAITING` entry |
| List queue | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/queue` | JWT; `queue.read`; organization scope | IDs; optional query `status` in queue enum | response array ordered by check-in, 200 | 400 invalid enum; 401/403/404; read-only |
| Get queue entry | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}` | JWT; `queue.read`; organization scope | IDs | response, 200 | 400/401/403/404; read-only |
| Call | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/call` | JWT; `queue.update`; organization scope | IDs; no body | response, 200 | 400 invalid lifecycle; 401/403/404; `WAITING→CALLED`, timestamps/actor |
| Start serving | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/start-serving` | JWT; `queue.update`; organization scope | IDs; no body | response, 200 | 400 invalid lifecycle; 401/403/404; `CALLED→SERVING`, timestamps/actor |
| Complete queue | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/complete` | JWT; `queue.update`; organization scope | IDs; no body | response, 200 | 400 invalid lifecycle; 401/403/404; `SERVING→COMPLETED`; does not complete appointment/encounter |
| Cancel queue | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/cancel` | JWT; `queue.update`; organization scope | IDs; no body | response, 200 | 400 invalid lifecycle; 401/403/404; active state→`CANCELLED`; no cascade |
| Update queue note | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/queue/{queueEntryId}/note` | JWT; `queue.update`; organization scope | IDs; `UpdateQueueEntryNoteRequest` | response, 200 | 400 length or terminal-state rule; 401/403/404; nullable/blank normalized by domain; actor updated |

## Encounter and clinical note

| Operation | Method and exact path | Auth / permission / scope | Params / request | Response / success | Errors, preconditions and side effects |
|---|---|---|---|---|---|
| Start encounter | `POST /api/v1/organizations/{organizationId}/facilities/{facilityId}/encounters` | JWT; `encounter.create`; organization scope | IDs; `StartEncounterRequest` | `EncounterResponse`, 201 | 400 malformed; 401/403/404 queue/appointment; 409 unless queue=`SERVING`, appointment=`SCHEDULED`, or duplicate encounter; patient/practitioner derived server-side; creates encounter and core note transactionally |
| List encounters | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/encounters` | JWT; `encounter.read`; organization scope | IDs; optional query `status` in encounter enum | response array, 200 | 400 invalid enum; 401/403/404; read-only |
| Get encounter | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}` | JWT; `encounter.read`; organization scope | IDs | response, 200 | 400/401/403/404; read-only |
| Get clinical note | `GET /api/v1/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}/clinical-note` | JWT; `clinical_note.read`; organization scope | IDs | `ClinicalNoteResponse`, 200 | 400/401/403/404 encounter/note; read-only |
| Update clinical note | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}/clinical-note` | JWT; `clinical_note.update`; organization scope | IDs; `UpdateClinicalNoteRequest` | note response, 200 | 400 size/blank domain value; 401/403/404; **409 `ENCOUNTER_CLINICAL_NOTE_IMMUTABLE` when encounter is terminal**; updates one note and actor |
| Complete encounter | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}/complete` | JWT; `encounter.update`; organization scope | IDs; no body | encounter response, 200 | 401/403/404; **400 `ENCOUNTER_INVALID_STATUS_TRANSITION` for repeated/invalid terminal transition**; sets `COMPLETED`; no queue/appointment cascade |
| Cancel encounter | `PUT /api/v1/organizations/{organizationId}/facilities/{facilityId}/encounters/{encounterId}/cancel` | JWT; `encounter.update`; organization scope | IDs; `CancelEncounterRequest` | encounter response, 200 | 400 validation and **`ENCOUNTER_INVALID_STATUS_TRANSITION`**; 401/403/404; sets `CANCELLED`; no cascade |

### Encounter contract clarification

Current behavior deliberately distinguishes two exception types. `Encounter.ensureInProgress()` throws `DomainValidationException`, so repeated/invalid complete or cancel maps to **HTTP 400** with `ENCOUNTER_INVALID_STATUS_TRANSITION`. `UpdateClinicalNoteUseCase` throws `ResourceStateConflictException` for a terminal encounter, so note mutation maps to **HTTP 409** with `ENCOUNTER_CLINICAL_NOTE_IMMUTABLE`. Converting invalid lifecycle transitions from 400 to 409 is a **[REQUIRES POLICY DECISION]** future API change and would require code, client and contract-test changes; it is not current behavior.

## Transaction and retry contract

Application use cases that write are Spring transactions through repository adapters; multi-write operations such as encounter plus initial clinical note are expected to be atomic at that boundary. Clients may safely retry GET after transient failure. Clients must not blindly retry POST/PUT when the outcome is unknown: the current API has no idempotency-key or entity-version contract. A target contract may introduce idempotency for creates, optimistic concurrency, pagination, 422 and 429; none is current.

## Related documents

- [Authorization](07-AUTHORIZATION.md)
- [Database](05-DATABASE.md)
- [Domain model](03-DOMAIN-MODEL.md)
- [Mobile networking](mobile/MOBILE-NETWORKING.md)
- [Testing](08-TESTING.md)
