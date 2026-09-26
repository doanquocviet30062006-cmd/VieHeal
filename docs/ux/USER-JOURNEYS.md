# User journeys

## Reception to encounter

1. Receptionist authenticates and selects assigned facility.
2. Searches/selects a patient and creates or locates an appointment.
3. Checks in the scheduled appointment; handles 409 if already queued or invalid state.
4. Calls the next entry and moves it to serving.
5. Doctor opens that serving entry and starts the encounter; patient/practitioner are derived by server.
6. Doctor edits the clinical note, optionally reviews an AI draft, then completes encounter.
7. Authorized staff complete queue and appointment separately because encounter completion does not cascade.

At every step: display progress, permission boundary, current status, safe recovery and last-sync freshness.

## Failure journey

During note save, connectivity fails. The app retains the draft, labels it unsent and does not claim success. On reconnect it reloads the encounter/version; if server state changed, it shows a conflict and requires reconciliation. Session expiry routes to login without leaking protected content; draft retention follows an approved security policy.
