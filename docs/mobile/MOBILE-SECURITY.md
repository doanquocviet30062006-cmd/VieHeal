# Android security model

Status: **[IMPLEMENTED]** for `allowBackup=false`, production cleartext denial, AppAuth OIDC/PKCE, Android Keystore AES-GCM token protection, redacted token models, and local logout cleanup. Clinical-data storage and release artifact scanning remain later work.

| Area | Target control | Decision / verification |
|---|---|---|
| secrets | no password, confidential OIDC secret, DB or AI key in source/resources/BuildConfig/native libs/APK | decompile and secret-scan release artifact |
| Keystore | Android Keystore non-exportable AES-256 key protects AES-GCM session ciphertext in private preferences; corruption deletes unusable state | lifecycle tests plus device extraction/process-death verification |
| DataStore | non-secret preferences and active scope IDs only | file inspection and code review |
| Room | minimized scoped cache; encryption library **[REQUIRES POLICY DECISION]** | threat/performance/backup analysis; DAO cleanup tests |
| backup | `allowBackup`/data-extraction rules exclude token, Room and protected drafts unless formally designed | adb/cloud backup/restore inspection |
| screen capture | `FLAG_SECURE` per high-risk screen is **[REQUIRES POLICY DECISION]** due accessibility/support trade-off | screenshot/recents tests |
| clipboard | clinical copy disabled by default; if permitted, warning/auto-clear where supported and no sensitive label preview | copy/paste tests |
| notifications | generic content only; no patient name, diagnosis or note on lock screen | notification privacy tests |
| OIDC redirect | AppAuth's exported receiver accepts only the configured scheme; transaction state is validated and a callback never bypasses `/auth/me` | merged-manifest review and malicious callback tests |
| WebView | prohibited for clinic content by default; system browser/custom tab for OIDC | dependency/source inspection |
| logs | release logging minimized; no token, IDs tied to PHI, bodies, note or prompt | logcat scan under failure |
| crash/analytics | allowlisted metadata only; scrub keys/breadcrumbs; vendor/privacy review | forced crash/event export inspection |
| rooted device | detection is a risk signal; block/warn policy **[REQUIRES POLICY DECISION]** | rooted/emulator tests; server remains trust boundary |
| debug builds | distinct app ID/backend, debug certificate, synthetic data; never access production | environment/build checks |
| release hardening | debuggable false, R8/minification/resource shrink, mapping custody, signed AAB | Gradle task/artifact inspection |
| network | cleartext denied in main/release; debug overlay permits emulator-local HTTP only; production configuration validation requires HTTPS | merged manifests and MITM tests |
| pinning | not automatic; benefits weighed against certificate rotation/outage and emergency update | explicit architecture/operations decision |
| screenshots/files | report uses synthetic data; no unmanaged clinical export | manual review |

Data lifecycle is user+organization+facility partitioned. Logout/scope change cancels jobs, clears memory/images, deletes Room rows/drafts and removes tokens before another identity renders. Release crash symbols/mappings are protected because they can reveal structure even though they should contain no PHI.

Mobile authorization is presentational only: hiding an action never substitutes for backend permission. Treat device time/connectivity/root status as untrusted. Do not store a “logged-in role” as an authorization truth; use the current backend access context.

## Related documents

[Security threat model](../06-SECURITY.md), [Authentication](MOBILE-AUTH.md), [Data](MOBILE-DATA.md), [Release](MOBILE-RELEASE.md).
