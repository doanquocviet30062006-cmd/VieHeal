# ADR-005: Minimized Room cache and local storage

## Status

Current architecture decision record. **Proposed [TARGET PRODUCTION DESIGN]**; Room/DataStore are not implemented.

## Context

Staff need resilient reads, but phones increase loss, extraction and stale-data risk. PostgreSQL is authoritative.

## Problem

Define what survives network loss, process death, logout and scope change without replicating the medical database.

## Decision

Use Room for scoped operational summaries/sync metadata and carefully reviewed drafts; DataStore for non-secret preferences; Keystore-backed storage for tokens. TTL and invalidation label stale data. Clear protected scope on logout/user/scope switch. Clinical notes are not cached by default.

## Decision Drivers

Offline read usability, structured queries/migrations, observable data and data minimization.

## Alternatives Considered

Network-only; cache everything; DataStore/JSON for records; mobile as system of record; encrypted DB by default.

## Reasons for Rejection / Trade-offs

Network-only harms resilience; full cache increases PHI risk; preferences storage is unsuitable for relational data; mobile authority violates architecture. Encryption choice needs library/operations analysis.

## Consequences

### Positive consequences

Fast scoped lists, explicit freshness and testable synchronization.

### Negative consequences

Cache migrations, invalidation complexity and logout cleanup are mandatory.

## Risks

Stale decisions, cross-user residue, backup leakage and failed Room migration.

## Operational Impact

Room schema versions/migrations ship with app; telemetry measures sync age without PHI.

## Security Impact

Minimize rows/fields, partition keys by user/org/facility, backup exclusions and extraction tests. Encryption remains a policy/technical decision.

## Testing Impact

DAO, migration, TTL, invalidation, stale display, logout/scope cleanup and conflict tests.

## Revisit Conditions

Offline clinical writes become a validated requirement or risk assessment mandates/removes encrypted Room.

## Related Documents

[Mobile data](../mobile/MOBILE-DATA.md), [Offline](../mobile/MOBILE-OFFLINE.md), [Database](../05-DATABASE.md).
