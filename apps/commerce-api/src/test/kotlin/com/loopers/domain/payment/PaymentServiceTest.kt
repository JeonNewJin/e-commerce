package com.loopers.domain.payment

import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.PaymentMethod.POINT
import com.loopers.domain.payment.entity.Payment
import com.loopers.domain.payment.model.CardType.HYUNDAI
import com.loopers.domain.payment.model.PaymentStatus.PENDING
import com.loopers.domain.payment.model.PaymentStatus.SUCCESS
import com.loopers.infrastructure.payment.PaymentJpaRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.event.ApplicationEvents
import java.math.BigDecimal
import java.util.UUID

class PaymentServiceTest(
    @SpykBean
    private val paymentService: PaymentService,
    @MockkBean
    private val cardPaymentProcessor: CardPaymentProcessor,
    @MockkBean
    private val pointPaymentProcessor: PointPaymentProcessor,
    private val paymentJpaRepository: PaymentJpaRepository,
) : IntegrationTestSupport() {

    @Autowired
    lateinit var applicationEvents: ApplicationEvents

    @BeforeEach
    fun setUp() {
        every { cardPaymentProcessor.paymentMethod() } returns CARD
        every { cardPaymentProcessor.process(any()) } just Runs
        every { pointPaymentProcessor.paymentMethod() } returns POINT
        every { pointPaymentProcessor.process(any()) } just Runs
    }

    @Nested
    inner class `결제를 할 때, ` {

        @Test
        fun `카드 방식으로 결제를 요청하면, 카드 결제 방식으로 프로세스가 진행된다`() {
            // given
            val command = PaymentCommand.Pay(
                userId = 1L,
                orderCode = UUID.randomUUID().toString(),
                amount = BigDecimal(10_000L),
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-1234-1234-1234",
            )

            // when
            paymentService.pay(command)

            // then
            verify(exactly = 1) { cardPaymentProcessor.process(command) }
        }

        @Test
        fun `포인트 방식으로 결제를 요청하면, 포인트 결제 방식으로 프로세스가 진행된다`() {
            // given
            val command = PaymentCommand.Pay(
                userId = 1L,
                orderCode = UUID.randomUUID().toString(),
                amount = BigDecimal(10_000L),
                paymentMethod = POINT,
            )

            // when
            paymentService.pay(command)

            // then
            verify(exactly = 1) { pointPaymentProcessor.process(command) }
        }
    }

    @Nested
    inner class `결제를 완료할 때, ` {

        @Test
        fun `주문 코드에 해당하는 결제 정보를 찾지 못하면, NOT_FOUND 예외가 발생한다`() {
            // given
            val orderCode = UUID.randomUUID().toString()
            val command = PaymentCommand.Complete(
                orderCode = orderCode,
                transactionKey = "transaction-key",
                paidAt = "2023-10-01T12:00:00",
            )

            // when
            val actual = assertThrows<CoreException> {
                paymentService.complete(command)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("결제 정보를 찾을 수 없습니다. 주문 코드: $orderCode") },
            )
        }

        @Test
        fun `정상적으로 완료 처리하고, 저장한다`() {
            // given
            val orderCode = UUID.randomUUID().toString()

            val payment = Payment(
                orderCode = orderCode,
                userId = 1L,
                amount = BigDecimal(10_000L),
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-1234-1234-1234",
                status = PENDING,
            )
            paymentJpaRepository.save(payment)

            val command = PaymentCommand.Complete(
                orderCode = orderCode,
                transactionKey = "transaction-key",
                paidAt = "2023-10-01T12:00:00",
            )

            // when
            paymentService.complete(command)

            // then
            val actual = paymentJpaRepository.findByOrderCode(orderCode)!!

            assertThat(actual.status).isEqualTo(SUCCESS)
            assertThat(actual.transactionKey).isEqualTo("transaction-key")
            assertThat(actual.paidAt).isEqualTo("2023-10-01T12:00:00")
        }

        @Test
        fun `정상적으로 완료 처리하고, 결제 완료 이벤트를 발행한다`() {
            // given
            val orderCode = UUID.randomUUID().toString()

            val payment = Payment(
                orderCode = orderCode,
                userId = 1L,
                amount = BigDecimal(10_000L),
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-1234-1234-1234",
                status = PENDING,
            )
            paymentJpaRepository.save(payment)

            val command = PaymentCommand.Complete(
                orderCode = orderCode,
                transactionKey = "transaction-key",
                paidAt = "2023-10-01T12:00:00",
            )

            // when
            paymentService.complete(command)

            // then
            val eventCount = applicationEvents.stream(PaymentEvent.PaymentCompleted::class.java)
                .filter { it.orderCode == orderCode && it.transactionKey == "transaction-key" }
                .count()

            assertThat(eventCount).isEqualTo(1)
        }
    }
}
