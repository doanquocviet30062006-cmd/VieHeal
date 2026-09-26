# ADR-004: Android application architecture

## Status

Current architecture decision record. **Proposed [TARGET PRODUCTION DESIGN]**; no Android source exists.

## Context

Android carries major course weight and must support authenticated, stateful clinic workflows rather than act as a static screen set.

## Problem

The client must remain testable across configuration change, process death, unreliable networks, permissions and offline cache while avoiding direct infrastructure coupling.

## Decision

Use Kotlin, Compose, single Activity, typed Navigation Compose, ViewModel, immutable StateFlow/UDF, coroutines, DI, use cases and repository contracts. Feature modules depend on core contracts; data implementations own Retrofit/Room.

## Decision Drivers

Lifecycle support, deterministic state, Compose ecosystem, testability, explicit data flow and modular ownership.

## Alternatives Considered

Fragments/XML; multi-Activity; mutable shared singleton state; feature UI calling Retrofit/DAO directly; cross-platform framework.

## Reasons for Rejection / Trade-offs

Alternatives either diverge from target skills/current direction, obscure state ownership or introduce an unselected ecosystem. Compose/UDF adds up-front modeling and build modules.

## Consequences

### Positive consequences

Clear event→state flow, replaceable repositories, lifecycle-aware collection and focused tests.

### Negative consequences

Boilerplate models/mappers; module and navigation API discipline; engineers need coroutine/Compose proficiency.

## Risks

Over-modularization, duplicated models, state-flag contradictions and accidental infrastructure imports.

## Operational Impact

Build variants, signing, R8, crash/ANR monitoring and staged rollout become part of release engineering.

## Security Impact

Backend remains enforcement; routes carry IDs only; tokens use protected storage; cache is scoped/minimized.

## Testing Impact

Unit, reducer/ViewModel, repository, Room, navigation, Compose accessibility, integration and E2E layers.

## Revisit Conditions

Prototype demonstrates build-time/productivity harm or platform requirements incompatible with the decision.

## Related Documents

[Mobile architecture](../mobile/MOBILE-ARCHITECTURE.md), [Modules](../mobile/MOBILE-MODULES.md), [State](../mobile/MOBILE-STATE-MANAGEMENT.md).
