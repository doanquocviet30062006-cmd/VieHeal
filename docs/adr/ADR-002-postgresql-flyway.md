# ADR-002: PostgreSQL and Flyway

## Status

Current architecture decision record. **Accepted and [IMPLEMENTED]**; V1–V24 are immutable.

## Context

Clinic workflows require durable relationships, transactional updates, tenant-safe constraints and repeatable environments.

## Problem

Schema ownership must not drift between JPA, developer machines and deployed databases.

## Decision

PostgreSQL is authoritative. Flyway is the only schema evolution mechanism; Hibernate uses `ddl-auto: validate`. Released migrations are append-only; the next version is V25. Room is never a replacement for PostgreSQL.

## Decision Drivers

Transactions, composite foreign keys, checks, GiST exclusion constraints, mature backup tooling and existing implementation.

## Alternatives Considered

Hibernate auto-DDL; manual SQL outside version control; document database; embedded/mobile database as server truth.

## Reasons for Rejection / Trade-offs

Auto-DDL and manual changes are not reproducible. A document store weakens relational invariants. Mobile storage cannot provide multi-user authority.

## Consequences

### Positive consequences

Auditable schema history, strong database enforcement and production-like Testcontainers tests.

### Negative consequences

Forward-only migration discipline, PostgreSQL-specific features and careful rollout compatibility are required.

## Risks

Destructive migration, long lock, extension availability or an edited historical migration can block rollout.

## Operational Impact

CI validates empty/upgraded schemas; production requires backup/readiness, migration monitoring and restore rehearsal.

## Security Impact

Use least-privilege runtime/migration roles, protected credentials, encrypted backups and tenant-safe constraints.

## Testing Impact

Test V1→latest and representative upgrade paths; exercise DB constraints directly as well as APIs.

## Revisit Conditions

A module has a justified independent datastore or PostgreSQL cannot meet a measured requirement.

## Related Documents

[Database](../05-DATABASE.md), [Deployment](../10-DEPLOYMENT.md), [Mobile data](../mobile/MOBILE-DATA.md).
