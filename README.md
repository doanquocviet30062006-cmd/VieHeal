# VieHeal

> Production-oriented clinic management platform with Android mobile application and AI-assisted clinical workflows.

## Documentation status

This repository uses the documentation under `docs/` as the canonical engineering specification for the current system and target production architecture.

Implementation state and documentation maturity are separate. Documents explicitly distinguish `[IMPLEMENTED]`, `[TARGET PRODUCTION DESIGN]`, `[PLANNED]`, `[EVIDENCE GAP]`, and other verification states.

## Current implementation

The verified backend is a Kotlin/Spring Boot modular monolith backed by PostgreSQL and Flyway migrations V1-V24, with JWT resource-server authentication integrated with Keycloak.

Implemented backend domains include:

- Organization and Facility
- IAM and authorization
- Patient
- Practitioner
- Service Catalog
- Scheduling
- Appointment
- Reception Queue
- Encounter
- Clinical Note

Android, Room, Figma, AI runtime and production deployment are documented as target designs unless implementation evidence exists.

## Target production architecture

The target system connects the Android application to the VieHeal backend API, PostgreSQL, Keycloak using OIDC Authorization Code + PKCE, and a backend-controlled AI Gateway. AI provider credentials must never be embedded in the Android application.

See [System Architecture](docs/02-ARCHITECTURE.md).

## Documentation map

### Core engineering

- [Project Overview](docs/00-PROJECT-OVERVIEW.md)
- [Product Requirements](docs/01-PRODUCT-REQUIREMENTS.md)
- [Architecture](docs/02-ARCHITECTURE.md)
- [Domain Model](docs/03-DOMAIN-MODEL.md)
- [API Specification](docs/04-API-SPEC.md)
- [Database](docs/05-DATABASE.md)
- [Security](docs/06-SECURITY.md)
- [Authorization](docs/07-AUTHORIZATION.md)
- [Testing](docs/08-TESTING.md)
- [Development](docs/09-DEVELOPMENT.md)
- [Deployment](docs/10-DEPLOYMENT.md)
- [Observability](docs/11-OBSERVABILITY.md)
- [Compliance](docs/12-COMPLIANCE.md)
- [Traceability](docs/13-TRACEABILITY.md)

### Android mobile

- [Mobile Product Scope](docs/mobile/MOBILE-PRODUCT-SCOPE.md)
- [Mobile Architecture](docs/mobile/MOBILE-ARCHITECTURE.md)
- [Mobile Modules](docs/mobile/MOBILE-MODULES.md)
- [Mobile Navigation](docs/mobile/MOBILE-NAVIGATION.md)
- [Mobile State Management](docs/mobile/MOBILE-STATE-MANAGEMENT.md)
- [Mobile UI/UX](docs/mobile/MOBILE-UI-UX.md)
- [Mobile Design System](docs/mobile/MOBILE-DESIGN-SYSTEM.md)
- [Mobile Data](docs/mobile/MOBILE-DATA.md)
- [Mobile Networking](docs/mobile/MOBILE-NETWORKING.md)
- [Mobile Authentication](docs/mobile/MOBILE-AUTH.md)
- [Mobile Security](docs/mobile/MOBILE-SECURITY.md)
- [Mobile Offline Strategy](docs/mobile/MOBILE-OFFLINE.md)
- [Mobile Error Handling](docs/mobile/MOBILE-ERROR-HANDLING.md)
- [Mobile Testing](docs/mobile/MOBILE-TESTING.md)
- [Mobile Performance](docs/mobile/MOBILE-PERFORMANCE.md)
- [Mobile Release](docs/mobile/MOBILE-RELEASE.md)

### AI

- [AI Architecture](docs/ai/AI-ARCHITECTURE.md)
- [AI Use Cases](docs/ai/AI-USE-CASES.md)
- [AI Prompt Design](docs/ai/AI-PROMPT-DESIGN.md)
- [AI Safety](docs/ai/AI-SAFETY.md)
- [AI Evaluation](docs/ai/AI-EVALUATION.md)
- [AI Limitations](docs/ai/AI-LIMITATIONS.md)

### UX and evidence

- [User Flows](docs/ux/USER-FLOWS.md)
- [Figma Requirements](docs/ux/FIGMA-REQUIREMENTS.md)
- [User Survey](docs/ux/USER-SURVEY.md)
- [Accessibility](docs/ux/ACCESSIBILITY.md)
- [Rubric Evidence Matrix](docs/rubric/RUBRIC-EVIDENCE-MATRIX.md)
- [Missing Evidence](docs/rubric/MISSING-EVIDENCE.md)
- [Demo Script](docs/rubric/DEMO-SCRIPT.md)
- [Submission Checklist](docs/rubric/SUBMISSION-CHECKLIST.md)

### Report and project management

- [Term Paper Outline](docs/report/TERM-PAPER-OUTLINE.md)
- [Report Page Budget](docs/report/REPORT-PAGE-BUDGET.md)
- [Screenshot Plan](docs/report/SCREENSHOT-PLAN.md)
- [Git Workflow](docs/project-management/GIT-WORKFLOW.md)
- [Issue Board Plan](docs/project-management/ISSUE-BOARD-PLAN.md)
- [Contribution Evidence](docs/project-management/CONTRIBUTION-EVIDENCE.md)

### Architecture decisions

Architecture Decision Records are maintained under [`docs/adr/`](docs/adr/).

## Important current security debt

`POST /api/v1/organizations` is currently documented as publicly accessible and is a **P0 security debt before production**. The implementation must be hardened and covered by integration tests before production deployment.

## Clinical workflow boundary

The current verified Encounter workflow requires a QueueEntry in `SERVING` state and an Appointment in `SCHEDULED` state. Patient and practitioner identity are derived by the server. Completing an Encounter does not automatically complete the QueueEntry or Appointment.

## Development direction

The recommended next implementation sequence is:

1. Close P0 backend security gaps.
2. Establish Android foundation and module architecture.
3. Implement OIDC/PKCE authentication.
4. Integrate real Patient and Appointment workflows.
5. Implement Queue → Encounter → Clinical Note mobile flow.
6. Add Room cache, offline states and Android tests.
7. Implement backend AI Gateway and clinician-review workflow.
8. Complete AI evaluation, observability, deployment hardening and rubric evidence.

## Documentation policy

Application behavior must not be inferred from target-design documents. Source code, migrations, tests and runtime configuration remain the implementation truth; documentation must be updated whenever those contracts change.
