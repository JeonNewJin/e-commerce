package com.loopers.interfaces.consumer.metrics

import com.loopers.config.kafka.KafkaConfig
import com.loopers.domain.event.EventHandledCommand
import com.loopers.domain.event.EventHandledService
import com.loopers.domain.metrics.CollectMethod
import com.loopers.domain.metrics.ProductMetricsCommand
import com.loopers.domain.metrics.ProductMetricsService
import jakarta.transaction.Transactional
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.shaded.com.google.protobuf.Timestamp
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders.OFFSET
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC
import org.springframework.kafka.support.KafkaHeaders.TIMESTAMP
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class MetricsEventsKafkaConsumer(
    private val eventHandledService: EventHandledService,
    private val productMetricsService: ProductMetricsService,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = ["catalog-events-v1"],
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "catalog-consumer-group",
    )
    @Transactional
    fun catalogListener(
        @Header(RECEIVED_TOPIC) topic: String,
        @Header(RECEIVED_PARTITION) partition: Int,
        @Header(OFFSET) offset: Long,
        @Header(TIMESTAMP) timestamp: Timestamp,
        messages: List<ConsumerRecord<Any, Any>>,
        acknowledgment: Acknowledgment,
    ) {
        val events = messages.mapNotNull { it.value() as? CatalogEventDto }
        if (events.isNotEmpty()) {
            val eventIds = events.map { it.eventId }.distinct()

            // 이미 처리된 이벤트 조회
            val alreadyHandles = eventHandledService.findAllByEventIds(eventIds).map { it.eventId }

            // 처리되지 않은 이벤트 필터링
            val notHandledEvents = events.filter { !alreadyHandles.contains(it.eventId) }.distinctBy { it.eventId }

            // 지표 수집
            notHandledEvents.forEach { productMetricsService.collect(it.toMetricsCommand()) }

            // 이벤트 처리 완료
            eventHandledService.saveAll(notHandledEvents.map { it.toHandledCommand(topic, partition, offset, timestamp) })
        }

        acknowledgment.acknowledge()
    }
}

data class CatalogEventDto(
    val eventId: String,
    val eventType: EventType,
    val productId: Long,
    val userId: Long? = null,
    val quantity: Int? = 0,
) {
    fun toMetricsCommand(): ProductMetricsCommand.Collect =
        ProductMetricsCommand.Collect(
            eventId = eventId,
            eventType = eventType.toCollectMethod(),
            productId = productId,
            userId = userId,
            quantity = quantity,
        )

    fun toHandledCommand(topic: String, partition: Int, offset: Long, timestamp: Timestamp): EventHandledCommand.Create =
        EventHandledCommand.Create(
            eventId = eventId,
            topic = topic,
            partition = partition,
            offset = offset,
            timestamp = timestamp,
        )

    enum class EventType {
        PRODUCT_LIKED,
        PRODUCT_UNLIKED,
        PRODUCT_VIEWED,
        STOCK_ADJUSTED,
        ;

        fun toCollectMethod(): CollectMethod = when (this) {
            PRODUCT_LIKED -> CollectMethod.PRODUCT_LIKED
            PRODUCT_UNLIKED -> CollectMethod.PRODUCT_UNLIKED
            PRODUCT_VIEWED -> CollectMethod.PRODUCT_VIEWED
            STOCK_ADJUSTED -> CollectMethod.STOCK_ADJUSTED
        }
    }
}
