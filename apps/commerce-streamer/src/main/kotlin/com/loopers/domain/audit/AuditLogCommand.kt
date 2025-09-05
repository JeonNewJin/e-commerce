package com.loopers.domain.audit

object AuditLogCommand {
    data class Create(val eventId: String, val payload: String)
}
