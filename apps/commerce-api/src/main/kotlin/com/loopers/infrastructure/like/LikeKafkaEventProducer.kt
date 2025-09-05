package com.loopers.infrastructure.like

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.domain.like.LikeEvent
import com.loopers.domain.like.LikeEventProducer
import com.loopers.infrastructure.common.CatalogMessage
import com.loopers.support.uuid.UUIDGenerator
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class LikeKafkaEventProducer(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
    private val uuidGenerator: UUIDGenerator,
    private val objectMapper: ObjectMapper,
) : LikeEventProducer {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val CATALOG_TOPIC = "catalog-events-v1"
    }

    override fun publish(event: LikeEvent.LikeCreated) {
        val message = CatalogMessage.from(event, uuidGenerator.generate())
        kafkaTemplate.send(
            CATALOG_TOPIC,
            event.targetId.toString(),
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

    override fun publish(event: LikeEvent.LikeDeleted) {
        val message = CatalogMessage.from(event, uuidGenerator.generate())
        kafkaTemplate.send(
            CATALOG_TOPIC,
            event.targetId.toString(),
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
