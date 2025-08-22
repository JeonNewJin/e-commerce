package com.loopers.domain.payment

import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.model.CardType.HYUNDAI
import com.loopers.infrastructure.payment.PaymentJpaRepository
import com.loopers.infrastructure.support.transaction.TransactionSynchronizationExecutor
import com.loopers.support.IntegrationTestSupport
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class CardPaymentProcessorTest(
    @SpykBean
    private val cardPaymentProcessor: CardPaymentProcessor,
    private val paymentJpaRepository: PaymentJpaRepository,
    @MockkBean
    private val transactionSynchronizationExecutor: TransactionSynchronizationExecutor,
) : IntegrationTestSupport() {

    @Nested
    open inner class `카드 방식으로 결제를 요청할 때, ` {

        @Test
        fun `결제 내역을 정상적으로 저장한다`() {
            // given
            every { transactionSynchronizationExecutor.afterCommit(any()) } answers {
                // do nothing
            }

            val orderCode = UUID.randomUUID().toString()
            val command = PaymentCommand.Pay(
                userId = 1L,
                orderCode = orderCode,
                amount = BigDecimal(10_000L),
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-1234-1234-1234",
            )

            // when
            cardPaymentProcessor.process(command)

            // then
            val actual = paymentJpaRepository.findAll()

            assertThat(actual).hasSize(1)
                .extracting("userId", "orderCode", "amount", "paymentMethod", "cardType", "cardNo")
                .containsExactlyInAnyOrder(
                    tuple(1L, orderCode, BigDecimal("10000.00"), CARD, HYUNDAI, "1234-1234-1234-1234"),
                )
        }

        @Test
        fun `결제 내역을 정상적으로 저장하면, PG에 결제를 정상 요청한다`() {
            // given
            every { transactionSynchronizationExecutor.afterCommit(any()) } answers {
                // do nothing
            }

            val orderCode = UUID.randomUUID().toString()
            val command = PaymentCommand.Pay(
                userId = 1L,
                orderCode = orderCode,
                amount = BigDecimal(10_000L),
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-1234-1234-1234",
            )

            // when
            cardPaymentProcessor.process(command)

            // then
            verify(exactly = 1) { transactionSynchronizationExecutor.afterCommit(any()) }
        }
    }
}
