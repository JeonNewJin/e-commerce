package com.loopers.domain.audit

interface AuditLogRepository {

    fun findAllByEventIds(eventIds: List<String>): List<AuditLog>

    fun saveAll(auditLog: List<AuditLog>)
}
