package com.loopers.domain.audit

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "audit_log")
class AuditLog(eventId: String, payload: String) : BaseEntity() {

    val eventId: String = eventId

    @Column(columnDefinition = "json")
    val payload: String = payload
}
