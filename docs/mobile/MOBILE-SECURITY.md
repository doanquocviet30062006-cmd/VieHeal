# Android security model

Status: **[TARGET PRODUCTION DESIGN]**.

| Area | Target control | Decision / verification |
|---|---|---|
| secrets | no password, confidential OIDC secret, DB or AI key in source/resources/BuildConfig/native libs/APK | decompile and secret-scan release artifact |
| Keystore | non-exportable keys protect persisted session ciphertext; tokens not raw Keystore values | extraction/process-death/rotation tests |
| DataStore | non-secret preferences and active scope IDs only | file inspection and code review |
| Room | minimized scoped cache; encryption library **[REQUIRES POLICY DECISION]** | threat/performance/backup analysis; DAO cleanup tests |
| backup | `allowBackup`/data-extraction rules exclude token, Room and protected drafts unless formally designed | adb/cloud backup/restore inspection |
| screen capture | `FLAG_SECURE` per high-risk screen is **[REQUIRES POLICY DECISION]** due accessibility/support trade-off | screenshot/recents tests |
| clipboard | clinical copy disabled by default; if permitted, warning/auto-clear where supported and no sensitive label preview | copy/paste tests |
| notifications | generic content only; no patient name, diagnosis or note on lock screen | notification privacy tests |
| deep links | verified/allowlisted route, IDs only, authenticate/authorize after resolution | malicious URI/fuzz tests |
| WebView | prohibited for clinic content by default; system browser/custom tab for OIDC | dependency/source inspection |
| logs | release logging minimized; no token, IDs tied to PHI, bodies, note or prompt | logcat scan under failure |
| crash/analytics | allowlisted metadata only; scrub keys/breadcrumbs; vendor/privacy review | forced crash/event export inspection |
| rooted device | detection is a risk signal; block/warn policy **[REQUIRES POLICY DECISION]** | rooted/emulator tests; server remains trust boundary |
| debug builds | distinct app ID/backend, debug certificate, synthetic data; never access production | environment/build checks |
| release hardening | debuggable false, R8/minification/resource shrink, mapping custody, signed AAB | Gradle task/artifact inspection |
| network | cleartext denied; modern TLS/system trust; network inspection debug-only | Network Security Config and MITM tests |
| pinning | not automatic; benefits weighed against certificate rotation/outage and emergency update | explicit architecture/operations decision |
| screenshots/files | report uses synthetic data; no unmanaged clinical export | manual review |

Data lifecycle is user+organization+facility partitioned. Logout/scope change cancels jobs, clears memory/images, deletes Room rows/drafts and removes tokens before another identity renders. Release crash symbols/mappings are protected because they can reveal structure even though they should contain no PHI.

Mobile authorization is presentational only: hiding an action never substitutes for backend permission. Treat device time/connectivity/root status as untrusted. Do not store a “logged-in role” as an authorization truth; use the current backend access context.

## Related documents

[Security threat model](../06-SECURITY.md), [Authentication](MOBILE-AUTH.md), [Data](MOBILE-DATA.md), [Release](MOBILE-RELEASE.md).
