# ADR-007: Appointment, queue, encounter and clinical-note boundaries

## Status

Current architecture decision record. **Accepted and [IMPLEMENTED]** for current modules.

## Context

Booking, physical presence and clinical care have related but different state machines and permissions.

## Problem

Collapsing them into one “visit” creates ambiguous ownership, cascades and unsafe client-supplied clinical identity.

## Decision

Appointment owns booking; QueueEntry owns reception presence; Encounter owns care session; ClinicalNote owns one narrative document. Start Encounter only when QueueEntry=`SERVING` and Appointment=`SCHEDULED`. Server derives patient/practitioner. One Encounter per Appointment and QueueEntry. Encounter completion never auto-completes QueueEntry or Appointment.

## Decision Drivers

Separation of responsibility, independent permissions/lifecycles, traceability, database integrity and explicit operations.

## Alternatives Considered

Single Visit aggregate; encounter from appointment without queue; client supplies patient/practitioner; automatic cascading completion.

## Reasons for Rejection / Trade-offs

A single aggregate mixes operational/clinical rules. Skipping queue loses presence state. Client identity is spoofable. Cascades conceal authorization and workflow decisions.

## Consequences

### Positive consequences

Clear ownership, server-derived identity, tenant-safe FKs and separately auditable transitions.

### Negative consequences

More endpoints and UI steps; incomplete operational states are possible until authorized actors finish each aggregate.

## Risks

Clients may assume completion cascades or mishandle partial workflow; concurrent transitions need conflict handling.

## Operational Impact

Monitoring should detect stale SERVING queues, long IN_PROGRESS encounters and mismatched terminal workflow for follow-up—not auto-fix silently.

## Security Impact

Encounter and note permissions are separate from queue; composite FKs block copied cross-tenant identities.

## Testing Impact

Test prerequisites, duplicates, server derivation, invalid terminal transition HTTP 400, terminal note HTTP 409 and non-cascading completion.

## Revisit Conditions

Validated clinic workflow requires walk-ins without appointments or an explicit transactional orchestration with approved authorization/audit semantics.

## Related Documents

[Domain model](../03-DOMAIN-MODEL.md), [API contract](../04-API-SPEC.md), [Database](../05-DATABASE.md), [User journeys](../ux/USER-JOURNEYS.md).
