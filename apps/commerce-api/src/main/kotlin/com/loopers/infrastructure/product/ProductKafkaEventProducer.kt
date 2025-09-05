package com.loopers.infrastructure.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.domain.product.ProductEvent
import com.loopers.domain.product.ProductEventProducer
import com.loopers.infrastructure.common.CatalogMessage
import com.loopers.support.uuid.UUIDGenerator
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductKafkaEventProducer(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
    private val uuidGenerator: UUIDGenerator,
    private val objectMapper: ObjectMapper,
) : ProductEventProducer {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val CATALOG_TOPIC = "catalog-events-v1"
    }

    override fun send(event: ProductEvent.ProductViewed) {
        val message = CatalogMessage.from(event, uuidGenerator.generate())
        kafkaTemplate.send(
            CATALOG_TOPIC,
            event.productId.toString(),
            objectMapper.writeValueAsString(message),
        ).whenComplete { result, ex ->
            if (ex == null) {
                val meta = result.recordMetadata
                logger.info("sent message with topic=${meta.topic()}, partition=${meta.partition()}, offset=${meta.offset()}")
            } else {
                logger.error(
                    "Error occurred while sending message to topic $CATALOG_TOPIC",
                    ex,
                )

                // DLT 토픽 발행
            }
        }
    }
}
