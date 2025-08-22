package com.loopers.domain.payment.entity

import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.model.CardType.HYUNDAI
import com.loopers.domain.payment.model.PaymentStatus.PENDING
import com.loopers.domain.payment.model.PaymentStatus.SUCCESS
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class PaymentTest {

    @Nested
    inner class `결제 정보를 생성할 때, ` {

        @Test
        fun `결재 금액이 0 이하의 정수이면, BAD_REQUEST 예외가 발생한다`() {
            // given
            val invalidAmount = BigDecimal.ZERO
            val userId = 1L
            val orderCode = "orderCode"
            val paymentMethod = CARD
            val cardType = HYUNDAI
            val cardNo = "1234-5678-9012-3456"
            val status = PENDING

            // when
            val actual = assertThrows<CoreException> {
                Payment(
                    amount = invalidAmount,
                    userId = userId,
                    orderCode = orderCode,
                    paymentMethod = paymentMethod,
                    cardType = cardType,
                    cardNo = cardNo,
                    status = status,
                )
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("결제 금액은 0보다 커야 합니다.") },
            )
        }

        @Test
        fun `카드 결제 시 카드 타입을 지정하지 않으면, BAD_REQUEST 예외가 발생한다`() {
            // given
            val amount = BigDecimal.ONE
            val userId = 1L
            val orderCode = "orderCode"
            val paymentMethod = CARD
            val cardNo = "1234-5678-9012-3456"
            val status = PENDING

            // when
            val actual = assertThrows<CoreException> {
                Payment(
                    amount = amount,
                    userId = userId,
                    orderCode = orderCode,
                    paymentMethod = paymentMethod,
                    cardType = null,
                    cardNo = cardNo,
                    status = status,
                )
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("카드 결제 시 카드 타입을 지정해야 합니다.") },
            )
        }

        @Test
        fun `카드 결제 시 카드 번호를 지정하지 않으면, BAD_REQUEST 예외가 발생한다`() {
            // given
            val amount = BigDecimal.ONE
            val userId = 1L
            val orderCode = "orderCode"
            val paymentMethod = CARD
            val cardType = HYUNDAI
            val status = PENDING

            // when
            val actual = assertThrows<CoreException> {
                Payment(
                    amount = amount,
                    userId = userId,
                    orderCode = orderCode,
                    paymentMethod = paymentMethod,
                    cardType = cardType,
                    cardNo = null,
                    status = status,
                )
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("카드 결제 시 카드 번호를 지정해야 합니다.") },
            )
        }

        @Test
        fun `정상 생성할 수 있다`() {
            // given
            val amount = BigDecimal.ONE
            val userId = 1L
            val orderCode = "orderCode"
            val paymentMethod = CARD
            val cardType = HYUNDAI
            val cardNo = "1234-5678-9012-3456"
            val status = PENDING

            // when
            val payment = Payment(
                amount = amount,
                userId = userId,
                orderCode = orderCode,
                paymentMethod = paymentMethod,
                cardType = cardType,
                cardNo = cardNo,
                status = status,
            )

            // then
            assertAll(
                { assertThat(payment.amount).isEqualTo(BigDecimal.ONE) },
                { assertThat(payment.userId).isEqualTo(1L) },
                { assertThat(payment.orderCode).isEqualTo("orderCode") },
                { assertThat(payment.paymentMethod).isEqualTo(CARD) },
                { assertThat(payment.cardType).isEqualTo(HYUNDAI) },
                { assertThat(payment.cardNo).isEqualTo("1234-5678-9012-3456") },
                { assertThat(payment.status).isEqualTo(PENDING) },
            )
        }
    }

    @Nested
    inner class `결제를 완료 처리할 때, ` {

        @Test
        fun `거래 키가 비어있으면, BAD_REQUEST 예외가 발생한다`() {
            // given
            val payment = Payment(
                amount = BigDecimal.ONE,
                userId = 1L,
                orderCode = "orderCode",
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-5678-9012-3456",
                status = PENDING,
            )

            val transactionKey = " "
            val paidAt = "2023-10-01T12:00:00"

            // when
            val actual = assertThrows<CoreException> {
                payment.complete(transactionKey, paidAt)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("거래 키는 비어있을 수 없습니다.") },
            )
        }

        @Test
        fun `결제 완료 시간이 비어있으면, BAD_REQUEST 예외가 발생한다`() {
            // given
            val payment = Payment(
                amount = BigDecimal.ONE,
                userId = 1L,
                orderCode = "orderCode",
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-5678-9012-3456",
                status = PENDING,
            )

            val transactionKey = "transactionKey"
            val paidAt = " "

            // when
            val actual = assertThrows<CoreException> {
                payment.complete(transactionKey, paidAt)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("결제 완료 시간은 비어있을 수 없습니다.") },
            )
        }

        @Test
        fun `성공적으로 완료 처리를 할 수 있다`() {
            // given
            val payment = Payment(
                amount = BigDecimal.ONE,
                userId = 1L,
                orderCode = "orderCode",
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-5678-9012-3456",
                status = PENDING,
            )

            val transactionKey = "transactionKey"
            val paidAt = "2023-10-01T12:00:00"

            // when
            payment.complete(transactionKey, paidAt)

            // then
            assertAll(
                { assertThat(payment.status).isEqualTo(SUCCESS) },
                { assertThat(payment.transactionKey).isEqualTo("transactionKey") },
                { assertThat(payment.paidAt).isEqualTo("2023-10-01T12:00:00") },
            )
        }
    }
}
