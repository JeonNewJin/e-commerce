package com.loopers.domain.audit

data class AuditLogInfo(val eventId: String, val payload: String) {
    companion object {
        fun from(auditLog: AuditLog): AuditLogInfo =
            AuditLogInfo(
                eventId = auditLog.eventId,
                payload = auditLog.payload,
            )
    }
}
