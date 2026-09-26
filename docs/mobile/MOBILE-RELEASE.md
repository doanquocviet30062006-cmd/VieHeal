# Android build configuration and release

Status: **[TARGET PRODUCTION DESIGN]**; no Android build/pipeline exists.

## Build environments

| Variant | API/OIDC | Tools/logging | Security/release |
|---|---|---|---|
| `debug` | local/emulator development URLs and dev issuer | debug menu, network inspector only with synthetic data, verbose redacted logs, optional test doubles | distinct application ID; debug key; never production data |
| `staging` | staging HTTPS API/issuer/public client | release-like telemetry; no mock infrastructure | staging app ID/signing; R8 rehearsal; synthetic/de-identified data |
| `release` | production HTTPS API/issuer/public client ID | no network/body inspection; minimal allowlisted logs | production signing, debuggable false, R8/resource shrink, cleartext denied |

Base URL, issuer and public client ID are non-secret environment configuration. Passwords, signing key material, refresh tokens and provider/database secrets never enter BuildConfig or repository. Signing uses protected CI/local credential facilities; document key custody, rotation/recovery and certificate fingerprint.

## Release gates

1. Compile/lint/static/dependency/license checks; unit/Room/navigation/Compose tests.
2. Staging OIDC/API/E2E and AI evaluation against exact backend/prompt versions.
3. Release APK/AAB decompile, secret, debuggable, backup, network-security and PHI telemetry inspection.
4. Accessibility, performance, crash/ANR and device matrix review.
5. Privacy notice, Play Data safety answers, support/deletion process and legal review.
6. Internal→closed→staged production rollout with server-side AI kill switch and rollback criteria.

Evidence manifest records commit, version code/name, application ID, build timestamp, signing fingerprint, backend commit/image, Flyway level, Keycloak realm/client version, AI provider/model/prompt/schema versions and test report links. The final demo video must use this same submitted build.

Rollback means halt rollout/redeploy previous compatible backend or app; Play rollback cannot instantly remove installed clients, so API compatibility and feature flags are required. Store R8 mapping/native symbols with restricted access for every release.

## Related documents

[Mobile security](MOBILE-SECURITY.md), [Testing](MOBILE-TESTING.md), [Deployment](../10-DEPLOYMENT.md), [Submission checklist](../rubric/SUBMISSION-CHECKLIST.md).
