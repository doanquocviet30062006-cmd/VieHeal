# Information architecture

Primary authenticated navigation: Dashboard, Appointments, Queue, Patients and Profile. Encounter and Clinical Note are contextual destinations reached from queue/appointment/patient; AI Assistant is subordinate to Clinical Note, not a general chatbot. Admin/service/schedule configuration can be a separate role-gated area or excluded from MVP.

Labels use clinic vocabulary validated through research. Search results expose minimum identifying information needed to avoid mistaken identity. Organization/facility context remains visible and switchable from a controlled entry point. Status, time zone and last refresh appear where operationally relevant.

The hierarchy prevents clinical records from being discoverable through unrestricted global search and prevents patient self-service from sharing the staff navigation model. Deep links resolve IDs only after login and scope checks.
