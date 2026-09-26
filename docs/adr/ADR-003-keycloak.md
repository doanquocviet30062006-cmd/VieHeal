# ADR-003: Keycloak and OIDC identity

## Status

Current architecture decision record. Backend JWT validation is **[IMPLEMENTED]**; hardened realms and Android PKCE are **[TARGET PRODUCTION DESIGN]**.

## Context

VieHeal separates external identity from internal organization memberships, roles, permissions and facility assignments.

## Problem

Mobile and backend need standards-based authentication without embedding passwords or confidential secrets in Android.

## Decision

Use Keycloak as OIDC issuer. Backend validates JWT and resolves `sub` to IAM. Android is a public client using Authorization Code + PKCE and system browser. Authorization remains in VieHeal IAM.

## Decision Drivers

OIDC standards, centralized sessions/MFA potential, Spring resource-server support and current development infrastructure.

## Alternatives Considered

Custom password/JWT service; direct credentials grant; confidential Android client; managed external IdP.

## Reasons for Rejection / Trade-offs

Custom identity increases security liability. Password grant and mobile client secret are inappropriate. A managed IdP remains possible but is not selected.

## Consequences

### Positive consequences

No password handling in API; standard token flow; identity and tenant authorization remain separable.

### Negative consequences

Realm/client lifecycle, availability, backups, upgrades and configuration hardening become operational responsibilities.

## Risks

Broad redirects, dev mode, wrong audience, long token lifetime or administrator compromise.

## Operational Impact

Separate database, health/readiness, TLS/DNS, export/backup, rotation and incident procedures are needed.

## Security Impact

PKCE/state/nonce, exact redirect URI, audience validation, short tokens and revocation policy are required. No confidential secret in APK.

## Testing Impact

Issuer/audience/expiry/signature tests; browser cancel; redirect attack; refresh concurrency; logout/revocation; inactive IAM mapping.

## Revisit Conditions

Hosting/compliance/availability requirements justify a managed IdP or federation strategy.

## Related Documents

[Authorization](../07-AUTHORIZATION.md), [Mobile auth](../mobile/MOBILE-AUTH.md), [Security](../06-SECURITY.md).
