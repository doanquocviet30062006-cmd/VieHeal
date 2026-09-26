# ADR-001: Modular monolith backend

## Status

Current architecture decision record. **Accepted and [IMPLEMENTED]** for the backend; service extraction remains conditional.

## Context

VieHeal currently contains organization, IAM, patient, practitioner, catalog, scheduling, appointment, reception and encounter capabilities in one Spring Boot deployable. Each module separates API, application, domain and persistence adapter packages.

## Problem

The system needs clear healthcare workflow boundaries and testability without imposing distributed-system operations on a small project.

## Decision

Keep one deployable modular monolith. Modules expose application/domain contracts; controllers do not bypass use cases; persistence adapters implement repository ports. Cross-module orchestration occurs in application services while PostgreSQL provides transactions.

## Decision Drivers

Small team, shared transactions, rapid delivery, one operational unit, existing source structure, and need for traceable module ownership.

## Alternatives Considered

Microservices per domain; unstructured layered monolith; serverless functions.

## Reasons for Rejection / Trade-offs

Microservices add network consistency, deployment and observability cost before independent scaling is proven. An unstructured monolith makes boundaries unenforceable. Functions complicate long transactions and local development.

## Consequences

### Positive consequences

Simple deployment, local debugging and transactional consistency; module packages map clearly to documentation/tests.

### Negative consequences

One release cadence and process; accidental cross-module coupling is possible; scaling is coarse-grained.

## Risks

Shared database access may erode ownership; one faulty module can affect the process.

## Operational Impact

One backend artifact and health surface; PostgreSQL remains a shared dependency. Capacity planning scales the service as a unit.

## Security Impact

One compromise has broad reach; module authorization and least-privilege database/runtime credentials are mandatory.

## Testing Impact

Module unit tests plus full controller/security/PostgreSQL integration tests; architecture tests should prevent forbidden dependencies.

## Revisit Conditions

Independent scaling, release cadence, compliance isolation, ownership or failure containment becomes measurable and material.

## Related Documents

[Architecture](../02-ARCHITECTURE.md), [Domain model](../03-DOMAIN-MODEL.md), [Testing](../08-TESTING.md).
