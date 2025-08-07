package com.loopers.domain.order

import com.loopers.domain.order.model.OrderStatus.PAYMENT_COMPLETED
import com.loopers.domain.order.model.OrderStatus.PAYMENT_PENDING
import com.loopers.domain.order.entity.Order
import com.loopers.domain.order.entity.OrderLine
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class OrderTest {

    @Nested
    inner class `주문을 생성할 때, ` {

        @Test
        fun `주문 상품 목록이 비어있으면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val userId = 1L
            val emptyOrderLines = emptyList<OrderLine>()

            // When
            val actual = assertThrows<CoreException> {
                Order(
                    userId = userId,
                    orderLines = emptyOrderLines,
                    status = PAYMENT_PENDING,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("주문 항목은 비어 있을 수 없습니다.") },
            )
        }

        @Test
        fun `주문이 정상 생성되고 총 주문 금액을 계산할 수 있다`() {
            // Given
            val userId = 1L
            val orderLines = listOf(
                OrderLine(productId = 1L, quantity = 1, unitPrice = BigDecimal(10_000L)),
                OrderLine(productId = 2L, quantity = 2, unitPrice = BigDecimal(20_000L)),
            )

            // When
            val actual = Order(
                userId = userId,
                orderLines = orderLines,
                status = PAYMENT_PENDING,
            )

            // Then
            assertAll(
                { assertThat(actual.userId).isEqualTo(1L) },
                {
                    assertThat(actual.orderLines).hasSize(2)
                        .extracting("productId", "quantity", "unitPrice")
                        .containsExactlyInAnyOrder(
                            tuple(1L, 1, BigDecimal(10_000L)),
                            tuple(2L, 2, BigDecimal(20_000L)),
                        )
                },
                { assertThat(actual.totalPrice).isEqualTo(BigDecimal(50_000L)) },
            )
        }
    }

    @Nested
    inner class `결제를 완료할 때, ` {

        @Test
        fun `이미 결제 완료된 주문은 BAD_REQUEST 예외가 발생한다`() {
            // Given
            val userId = 1L
            val orderLines = listOf(
                OrderLine(productId = 1L, quantity = 1, unitPrice = BigDecimal(10_000L)),
            )
            val order = Order(
                userId = userId,
                orderLines = orderLines,
                status = PAYMENT_COMPLETED,
            )

            // When
            val actual = assertThrows<CoreException> {
                order.completePayment()
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("주문 상태가 결제 대기 중이 아닙니다. 현재 상태: PAYMENT_COMPLETED") },
            )
        }

        @Test
        fun `결제 대기 상태에서 결제 완료로 상태가 변경된다`() {
            // Given
            val userId = 1L
            val orderLines = listOf(
                OrderLine(productId = 1L, quantity = 1, unitPrice = BigDecimal(10_000L)),
            )
            val order = Order(
                userId = userId,
                orderLines = orderLines,
                status = PAYMENT_PENDING,
            )

            // When
            order.completePayment()

            // Then
            assertThat(order.status).isEqualTo(PAYMENT_COMPLETED)
        }
    }
}
