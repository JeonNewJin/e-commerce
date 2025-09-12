package com.loopers.interfaces.consumer.catalog

import com.loopers.config.kafka.KafkaConfig
import com.loopers.domain.catalog.CatalogCommand
import com.loopers.domain.catalog.CatalogService
import com.loopers.domain.catalog.EventHandleMethod
import jakarta.transaction.Transactional
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class CatalogEventsKafkaConsumer(private val catalogService: CatalogService) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = ["catalog-events-v1"],
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "catalog-consumer-group",
    )
    @Transactional
    fun catalogListener(
        messages: List<ConsumerRecord<Any, Any>>,
        acknowledgment: Acknowledgment,
    ) {
        val events = messages.mapNotNull { it.value() as? CatalogEventDto }

        events.forEach { event ->
            catalogService.handle(event.toCatalogCommand())
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
    fun toCatalogCommand(): CatalogCommand.Handle =
        CatalogCommand.Handle(
            eventId = eventId,
            eventType = eventType.toEventHandleMethod(),
            productId = productId,
            userId = userId,
            quantity = quantity,
        )

    enum class EventType {
        PRODUCT_LIKED,
        PRODUCT_UNLIKED,
        PRODUCT_VIEWED,
        STOCK_ADJUSTED,
        ;

        fun toEventHandleMethod(): EventHandleMethod = when (this) {
            PRODUCT_LIKED -> EventHandleMethod.PRODUCT_LIKED
            PRODUCT_UNLIKED -> EventHandleMethod.PRODUCT_UNLIKED
            PRODUCT_VIEWED -> EventHandleMethod.PRODUCT_VIEWED
            STOCK_ADJUSTED -> EventHandleMethod.STOCK_ADJUSTED
        }
    }
}
