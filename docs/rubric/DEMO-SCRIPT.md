# Demo script

Target runtime: 7:45, inside the required 5–10 minutes. Rehearse from the exact submitted release with synthetic persisted data. If a capability remains absent, remove it rather than simulate it.

| Time | Story and evidence |
|---|---|
| 0:00–0:30 | real clinic workflow problem, target users; disclose survey status accurately |
| 0:30–1:00 | one architecture diagram: Android→VieHeal API→PostgreSQL and backend→AI provider |
| 1:00–2:00 | real Keycloak login/PKCE, active facility and role-aware navigation; no fixed credentials in APK |
| 2:00–3:15 | load persisted appointments, create/edit one, show DB/API persistence and one validation/conflict |
| 3:15–4:15 | check in, call, serve, start encounter; explain server-derived patient/practitioner |
| 4:15–5:45 | create real AI draft through backend; show draft label, grounding/safety, edit/reject/accept; provider failure fallback if time |
| 5:45–6:30 | show Loading, Empty, Error and Success plus offline/stale or 403 |
| 6:30–7:15 | Testcontainers/Android/AI reports, PostgreSQL/Flyway evidence and Git graph |
| 7:15–7:45 | limitations, security debt and next production steps |

Presenter notes: encounter completion does not automatically complete queue or appointment—show or state their separate transitions. Never call local-only mock data “API data.” Blur no real data; use synthetic data from the start. Preflight network/provider availability and keep an honest prerecorded fallback segment only if course rules permit.

## Slide outline (10–14)

1 title; 2 problem/users; 3 research/survey; 4 requirements/scope; 5 overall architecture; 6 Android architecture; 7 UI/UX/prototype; 8 database/API workflow; 9 security; 10 AI design/safety; 11 testing; 12 Git/team; 13 demo/results; 14 limitations/roadmap. Slides are visual and cite the exact commit/build.
