package com.clinic.platform.modules.reception.domain

enum class QueueEntryStatus {
    WAITING,
    CALLED,
    SERVING,
    COMPLETED,
    CANCELLED
}