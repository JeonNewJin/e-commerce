package com.loopers.infrastructure.like

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.loopers.domain.like.LikeEvent
import com.loopers.domain.like.model.LikeableType.PRODUCT
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.uuid.UUIDGenerator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.time.Duration

@EmbeddedKafka(
    partitions = 1,
    topics = ["catalog-events-v1"],
    brokerProperties = [
        "listeners=PLAINTEXT://localhost:19092",
    ],
)
class LikeKafkaEventProducerTest(
    private val likeKafkaEventProducer: LikeKafkaEventProducer,
    @MockkBean
    private val uuidGenerator: UUIDGenerator,
) : IntegrationTestSupport() {

    @Autowired
    private lateinit var testKafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    private lateinit var testConsumerFactory: ConsumerFactory<String, Any>

    private val objectMapper = jacksonObjectMapper()

    @TestConfiguration
    internal class TestKafkaConfig {

        @Bean
        fun testProducerFactory(): ProducerFactory<String, Any> {
            val configProps = mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:19092",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            )
            return DefaultKafkaProducerFactory(configProps)
        }

        @Bean
        fun testKafkaTemplate(): KafkaTemplate<String, Any> = KafkaTemplate(testProducerFactory())

        @Bean
        fun testConsumerFactory(): ConsumerFactory<String, Any> {
            val configProps = mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:19092",
                ConsumerConfig.GROUP_ID_CONFIG to "test-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            )
            return DefaultKafkaConsumerFactory(configProps)
        }
    }

    @Test
    fun `좋아요 생성됨 이벤트를 발행하면, 카탈로그 메시지를 발행한다`() {
        // given
        val consumer = testConsumerFactory.createConsumer("test-group")
        consumer.subscribe(listOf("catalog-events-v1"))

        val event = LikeEvent.LikeCreated(
            userId = 1L,
            targetId = 100L,
            targetType = PRODUCT,
        )

        every { uuidGenerator.generate() } returns "test-event-id"

        // when
        likeKafkaEventProducer.publish(event)

        // then
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertThat(records).isNotEmpty()

        val record = records.records("catalog-events-v1").first()
        assertThat(record.value()).isNotNull()

        val recordValue = String(record.value() as ByteArray)
        val auditEvent = objectMapper.readTree(recordValue)

        assertThat(auditEvent.get("eventId").asText()).isEqualTo("test-event-id")
        assertThat(auditEvent.get("eventType").asText()).isEqualTo("PRODUCT_LIKED")
        assertThat(auditEvent.get("productId").asLong()).isEqualTo(100L)
        assertThat(auditEvent.get("userId").asLong()).isEqualTo(1L)

        consumer.close()
    }

    @Test
    fun `좋아요 삭제됨 이벤트를 발행하면, 카탈로그 메시지를 발행한다`() {
        // given
        val consumer = testConsumerFactory.createConsumer("test-group")
        consumer.subscribe(listOf("catalog-events-v1"))

        val event = LikeEvent.LikeDeleted(
            userId = 1L,
            targetId = 100L,
            targetType = PRODUCT,
        )

        every { uuidGenerator.generate() } returns "test-event-id"

        // when
        likeKafkaEventProducer.publish(event)

        // then
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertThat(records).isNotEmpty()

        val record = records.records("catalog-events-v1").first()
        assertThat(record.value()).isNotNull()

        val recordValue = String(record.value() as ByteArray)
        val auditEvent = objectMapper.readTree(recordValue)

        assertThat(auditEvent.get("eventId").asText()).isEqualTo("test-event-id")
        assertThat(auditEvent.get("eventType").asText()).isEqualTo("PRODUCT_UNLIKED")
        assertThat(auditEvent.get("productId").asLong()).isEqualTo(100L)
        assertThat(auditEvent.get("userId").asLong()).isEqualTo(1L)

        consumer.close()
    }
}
