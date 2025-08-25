package com.loopers.domain.payment

import com.loopers.domain.payment.PaymentMethod.POINT
import com.loopers.domain.point.PointWalletRepository
import com.loopers.domain.point.entity.PointWallet
import com.loopers.domain.point.vo.Point
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID

class PointPaymentProcessorTest(
    private val pointPaymentProcessor: PointPaymentProcessor,
    private val pointWalletRepository: PointWalletRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `포인트로 결제를 진행할 때, ` {

        @Test
        fun `포인트 지갑이 존재하지 않는 사용자 ID로 요청하면, NOT_FOUND 예외가 발생한다`() {
            // given
            val nonExistentUserId = 1L
            val command = PaymentCommand.Pay(
                userId = nonExistentUserId,
                orderCode = UUID.randomUUID().toString(),
                amount = BigDecimal(10_000L),
                paymentMethod = POINT,
            )

            // when
            val actual = assertThrows<CoreException> {
                pointPaymentProcessor.process(command)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 사용자의 포인트 지갑을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `잔액이 10,000 포인트인 사용자가 10,000 포인트 결제를 요청하면, 잔액은 0 포인트가 된다`() {
            // given
            val existentUserId = 1L
            pointWalletRepository.save(
                PointWallet(
                    userId = existentUserId,
                    balance = Point.of(10_000L),
                ),
            )

            val command = PaymentCommand.Pay(
                userId = existentUserId,
                orderCode = UUID.randomUUID().toString(),
                amount = BigDecimal(10_000L),
                paymentMethod = POINT,
            )

            // when
            pointPaymentProcessor.process(command)

            // then
            val actual = pointWalletRepository.findByUserId(existentUserId)!!

            assertThat(actual.balance.value).isEqualTo(BigDecimal("0.00"))
        }
    }
}
