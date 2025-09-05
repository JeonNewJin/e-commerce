package com.loopers.infrastructure.auditlog

import com.loopers.domain.audit.AuditLog
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogJpaRepository : JpaRepository<AuditLog, Long> {

    fun findAllByEventIdIn(eventIds: List<String>): List<AuditLog>
}
