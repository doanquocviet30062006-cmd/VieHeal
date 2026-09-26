# Mobile data and Room design

Status: **[TARGET PRODUCTION DESIGN] / [EVIDENCE GAP]**. Room does not exist. PostgreSQL remains authoritative. TTL values are initial policies to validate, not measured guarantees.

## Cache classification

| Classification | Data | Rationale |
|---|---|---|
| SAFE TO CACHE (within authenticated scope) | non-sensitive reference IDs/names for services, practitioner display summaries, active-scope metadata | required for selectors; still clear on user/scope change |
| CACHE WITH CARE | appointment summaries, queue entries, patient minimum identity, encounter metadata, unsent note draft only if policy approves | operational value but contains personal/health context; minimize, TTL, stale label, cleanup |
| DO NOT CACHE BY DEFAULT | full ClinicalNote, AI prompts/responses, diagnosis/history, attachments, credentials/tokens, audit payload | high sensitivity or wrong storage responsibility |

## Candidate entity policy

Every key includes `userId`, `organizationId` and the applicable `facilityId`; this prevents one signed-in identity or scope reading another's rows.

| Entity | Cache key and scope | TTL | Refresh / invalidation | Logout and scope switch | Offline behavior | Sensitivity |
|---|---|---:|---|---|---|---|
| `ServiceSummaryEntity` | user+org+facility+facilityServiceId | 24 h | refresh selector/open; invalidate service mutation/404 | delete old user/scope | read selector with stale label | low/moderate |
| `PractitionerSummaryEntity` | user+org+facility+practitionerId | 1 h | refresh appointment form/TTL; invalidate profile/scope | delete | stale read only | personal data |
| `PatientSummaryEntity` | user+org+facility+patientId; only code/name/minimum disambiguation | 10 min | refresh list/detail; invalidate patient update/404 | delete immediately | stale search/display; no bulk history | high |
| `AppointmentEntity` | user+org+facility+appointmentId | 5 min active day, 30 min historical | refresh list/detail/pull; invalidate any appointment/queue action/404 | delete | stale read; writes disabled | high |
| `QueueEntryEntity` | user+org+facility+queueEntryId | 30 s | foreground/refresh/action; invalidate related appointment/encounter events | delete | very visibly stale; no transition writes | high/time-critical |
| `EncounterSummaryEntity` | user+org+facility+encounterId | 60 s active, 10 min terminal | refresh/open/action; invalidate note/encounter transition/404 | delete | metadata read only | high |
| `ClinicalNoteDraftEntity` | user+org+facility+encounterId | max 24 h if approved | explicit local edit; delete on save/terminal/conflict resolution | secure delete | unsent draft only; never claim saved | very high; **policy decision** |
| `SyncMetadataEntity` | user+scope+resourceType/queryKey | follows resource | records attempt/success/server version/error | delete | drives stale timestamp | low if IDs protected |

## Representative Room schema

Entities use explicit column names, non-null scope columns and indexes such as `(user_id,organization_id,facility_id,status,sort_time)`. Child summaries may use foreign keys only when parent deletion semantics are safe; otherwise scope cleanup occurs in one transaction to avoid a large fragile graph. Never use `fallbackToDestructiveMigration` in release builds.

DAO ownership is per resource inside `core:database`; only the corresponding local data source calls it. Representative methods:

```kotlin
@Query("SELECT * FROM appointment_cache WHERE user_id=:user AND organization_id=:org AND facility_id=:facility ORDER BY scheduled_start_at")
fun observeAppointments(user: UUID, org: UUID, facility: UUID): Flow<List<AppointmentEntity>>

@Transaction suspend fun replaceAppointmentScope(scope: ScopeKey, rows: List<AppointmentEntity>)
@Query("DELETE FROM appointment_cache WHERE user_id=:user AND organization_id=:org AND facility_id=:facility")
suspend fun clearAppointmentScope(...)
```

Remote refresh maps DTO→domain→entity and commits rows plus sync metadata atomically. A failed response never replaces good cache with empty. A confirmed successful empty list replaces that query scope. A 404 deletes the item. A 401/403 stops refresh; 403 also invalidates data no longer authorized according to policy.

## Room versioning and migration

Room has its own integer schema version and `Migration(n,n+1)` chain; it is unrelated to Flyway V1–V24. Export Room schemas to version control, test each supported upgrade, test fresh install, and retain at least versions supported by app-upgrade policy. Destructive migration is allowed only for debug/test or after an explicit decision that all cached/draft data is disposable and the user is warned.

## Synchronization rules

Network-first writes; cache updates only from authoritative success. WorkManager may refresh idempotent reads with network/battery constraints and bounded backoff. Do not queue appointment, queue, encounter or note POST/PUT by default. If the product later requires offline note drafting, it remains a local draft and uses server version/ETag reconciliation before explicit save.

Scope switch sequence is atomic at the application level: cancel old requests → clear in-memory models/images → delete old protected scope in a Room transaction → switch active preference → load new scope. Logout additionally erases token state and every protected user row. Tests simulate crash between steps and verify no old-scope rendering.

## Storage boundaries

DataStore contains non-secret settings such as theme, locale and last selected scope ID; it never stores tokens or clinical payloads. OIDC/Keystore-backed security owns token material. Image/file caches are prohibited for clinical attachments until a separate encrypted file policy exists.

## Related documents

[Backend database](../05-DATABASE.md), [Offline](MOBILE-OFFLINE.md), [Security](MOBILE-SECURITY.md), [State management](MOBILE-STATE-MANAGEMENT.md), [ADR-005](../adr/ADR-005-mobile-local-storage.md).
