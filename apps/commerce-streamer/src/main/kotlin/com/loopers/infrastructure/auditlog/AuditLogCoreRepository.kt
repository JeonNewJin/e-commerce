package com.loopers.infrastructure.auditlog

import com.loopers.domain.audit.AuditLog
import com.loopers.domain.audit.AuditLogRepository
import org.springframework.stereotype.Component

@Component
class AuditLogCoreRepository(private val auditLogJpaRepository: AuditLogJpaRepository) : AuditLogRepository {

    override fun findAllByEventIds(eventIds: List<String>): List<AuditLog> {
        if (eventIds.isEmpty()) return emptyList()
        return auditLogJpaRepository.findAllByEventIdIn(eventIds)
    }

    override fun saveAll(auditLog: List<AuditLog>) {
        auditLogJpaRepository.saveAll(auditLog)
    }
}
