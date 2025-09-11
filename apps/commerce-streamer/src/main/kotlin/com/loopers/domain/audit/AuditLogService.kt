package com.loopers.domain.audit

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class AuditLogService(private val auditLogRepository: AuditLogRepository) {

    fun findAllByEventIds(eventIds: List<String>): List<AuditLogInfo> =
        auditLogRepository.findAllByEventIds(eventIds)
            .map { AuditLogInfo.from(it) }

    @Transactional
    fun saveAll(command: List<AuditLogCommand.Create>) {
        val auditLogs = command.map { AuditLog(eventId = it.eventId, payload = it.payload) }
        auditLogRepository.saveAll(auditLogs)
    }
}
