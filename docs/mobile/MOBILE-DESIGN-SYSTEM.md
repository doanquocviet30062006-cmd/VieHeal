# Mobile design system

**[TARGET PRODUCTION DESIGN]** Use Material 3 as the behavioral baseline with VieHeal tokens, not ad-hoc values.

| Token group | Initial specification |
|---|---|
| Color roles | primary/secondary/surface/error/warning/success/info; semantic roles in light/dark palettes |
| Typography | Material display/title/body/label scale; user font scaling supported |
| Spacing | 4dp base: 4, 8, 12, 16, 24, 32, 48 |
| Shape | small 8dp, medium 12dp, large 16dp; confirm in Figma |
| Elevation | minimal; use surface/color/border before shadow |
| Motion | short, reduced-motion friendly, never blocks clinical action |

Core components: app bar, navigation bar/rail, buttons, fields, search/filter, patient summary, appointment card, queue row, status chip, permission notice, stale/offline banner, empty/error panels, skeleton, confirmation dialog, snackbar and AI draft card. Every component documents enabled/disabled/loading/error/focus states, semantics and token bindings.

Clinical statuses must not rely on color alone. Red is reserved for errors/destructive meaning, not routine branding. Do not truncate critical identifiers or times without an accessible full value. The design source of truth will be Figma after creation; Compose tokens must be generated or manually reconciled with a recorded review.
