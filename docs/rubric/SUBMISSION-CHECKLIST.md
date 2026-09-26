# Submission checklist

## Product

- [ ] release Android app contains no fake backend, fixed user or embedded secret
- [ ] real OIDC login and PostgreSQL-persisted critical journey works
- [ ] AI calls a real provider through backend and requires clinician review
- [ ] Loading/Empty/Error/Success and permission/offline/conflict states work
- [ ] release APK/AAB passes secret and debug-configuration review

## Evidence

- [ ] real survey export/analysis; no fabricated response
- [ ] Figma frames/components/click-through match implemented UI
- [ ] backend, Android and 15 AI evaluations freshly executed and archived
- [ ] screenshots use synthetic data and map to rubric matrix
- [ ] Git graph, issue/board/PR evidence accurately represented
- [ ] 40–60 page report, references and appendices rendered/checked
- [ ] 10–14 slides and 5–10 minute video match submitted build

## Consistency/security

- [ ] implemented/target labels audited; V1–V24 unchanged; next migration V25
- [ ] encounter invariant and non-cascading completion stated consistently
- [ ] no unsupported legal compliance claims
- [ ] links, Mermaid, spelling, figure/table numbering checked
- [ ] `git diff --check`, `git status --short`, `git diff --stat` recorded
- [ ] commit, version, backend build and migration level included in evidence manifest
