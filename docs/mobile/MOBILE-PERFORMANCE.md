# Mobile performance plan

Status: **[NOT VERIFIED] — NOT YET MEASURED**. Budgets below are target hypotheses and release gates only after baselining on a named mid-range device, Android version, build type and network profile.

| Signal | Initial target | Measurement |
|---|---:|---|
| cold startup to first interactive shell | ≤2.5 s p95 | Macrobenchmark, release-like build |
| warm startup | ≤1.0 s p95 | Macrobenchmark |
| cached list first content | ≤500 ms p95 | trace ViewModel→Room→Compose |
| API screen feedback | progress within 100 ms; measured content budget ≤2 s on test network | correlation/mobile trace |
| scroll rendering | ≥95% frames within device refresh budget | JankStats/Macrobenchmark |
| Room common query | ≤100 ms p95 on capped cache | instrumentation benchmark/query plan |
| memory | baseline and leak-free navigation; numeric cap after measurement | profiler/leak checks |
| AI | progress immediately; latency distribution reported, not hidden | backend/mobile correlation |

Engineering tactics: Baseline Profiles; lazy lists/stable keys; immutable stable UI models; derived state; pagination before unbounded lists; debounce/cancel search; no large objects in navigation; transactional indexed Room queries; bounded caches; avoid polling storms on foreground; WorkManager only for deferrable idempotent refresh with network/battery constraints.

Analyze Compose recomposition counts for queue/list rows, allocations during navigation, app startup providers, network payload sizes, battery during background refresh and cold cache. Performance never justifies unlabeled stale clinical data or sensitive tracing. Publish device/build/network and raw report with each claim.

## Related documents

[Architecture](MOBILE-ARCHITECTURE.md), [Data](MOBILE-DATA.md), [Offline](MOBILE-OFFLINE.md), [Observability](../11-OBSERVABILITY.md).
