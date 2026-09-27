# Development guide

## Current backend

Prerequisites are Java 21, Docker and environment variables `CLINIC_DB_PASSWORD`, `KEYCLOAK_DB_PASSWORD`, and `KEYCLOAK_ADMIN_PASSWORD`; usernames have development defaults. Start dependencies with `docker compose -f infrastructure/docker-compose.dev.yml up -d`; Keycloak imports the secret-free `clinic-platform` realm and public `vieheal-android` client. Add development users through Keycloak administration and map their subjects to IAM users; no credentials belong in the realm JSON. Then run `backend/gradlew.bat bootRun`. The backend defaults to the `dev` profile and port 8080; Keycloak development port is 8081; application PostgreSQL host port is 5433.

Never commit `.env`, credentials, exported realms containing secrets, tokens or patient data. Use synthetic records for development. Keep `open-in-view=false` and `ddl-auto=validate`; schema changes use a new Flyway migration beginning at V26.

## Engineering workflow

Branch per issue, make narrowly scoped conventional commits, run formatting/static analysis when configured, execute relevant tests, update documentation and open a reviewed PR. Definition of done includes authorization, tenant isolation, validation, error contract, migration safety, tests and observable behavior. The current repository has no CI or PR evidence, so this section is target process.

## Android authentication setup

The Android project is under `mobile`. Put the non-secret issuer, public client ID, redirect URI/scheme, and backend URL in the developer's Gradle user properties as documented in `mobile/MOBILE-AUTH.md`. Never add a client secret. With the Compose services and backend running, launch the debug app on an emulator, sign in through the system browser, and confirm Home appears only after `/api/v1/auth/me` returns a mapped IAM context. Release values must use HTTPS; local cleartext is debug-only. Use test fixtures only in test source sets and prohibit production code from depending on fake authentication or a fake backend.
