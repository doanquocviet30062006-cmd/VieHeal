# Development guide

## Current backend

Prerequisites are Java 21, Docker and environment variables `CLINIC_DB_PASSWORD`, `KEYCLOAK_DB_PASSWORD`, and `KEYCLOAK_ADMIN_PASSWORD`; usernames have development defaults. Start dependencies with the development Compose file, configure a `clinic-platform` Keycloak realm and test users, then run `backend/gradlew.bat bootRun`. The backend defaults to the `dev` profile and port 8080; Keycloak development port is 8081; application PostgreSQL host port is 5433.

Never commit `.env`, credentials, exported realms containing secrets, tokens or patient data. Use synthetic records for development. Keep `open-in-view=false` and `ddl-auto=validate`; schema changes use a new Flyway migration beginning at V25.

## Engineering workflow

Branch per issue, make narrowly scoped conventional commits, run formatting/static analysis when configured, execute relevant tests, update documentation and open a reviewed PR. Definition of done includes authorization, tenant isolation, validation, error contract, migration safety, tests and observable behavior. The current repository has no CI or PR evidence, so this section is target process.

## Android target setup

Create a separate Android Gradle project with Kotlin/Compose, version catalogs, build variants (`debug`, `staging`, `release`) and injected non-secret base URLs/issuer/client ID. Keep secrets outside `BuildConfig`. Enforce module dependencies described in `mobile/MOBILE-MODULES.md`, use test fixtures only in test source sets, and prohibit production code from depending on a fake backend.
