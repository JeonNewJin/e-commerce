package com.loopers.interfaces.consumer.auditlog

import com.loopers.config.kafka.KafkaConfig
import com.loopers.domain.audit.AuditLogCommand
import com.loopers.domain.audit.AuditLogService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class AuditEventsKafkaConsumer(private val auditLogService: AuditLogService) {

    @KafkaListener(
        topics = ["catalog-events-v1"],
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "audit-log-consumer-group",
    )
    fun auditListener(
        messages: List<ConsumerRecord<Any, Any>>,
        acknowledgment: Acknowledgment,
    ) {
        val events = messages.mapNotNull { it.value() as? AuditEventDto }

        auditLogService.saveAll(events.map { it.toCommand() })

        acknowledgment.acknowledge()
    }
}

data class AuditEventDto(val eventId: String, val payload: String) {
    fun toCommand(): AuditLogCommand.Create =
        AuditLogCommand.Create(
            eventId = eventId,
            payload = payload,
        )
}
