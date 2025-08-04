package com.loopers.domain.order

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class OrderLineTest {

    @Nested
    inner class `주문 상품을 생성할 때, ` {

        @Test
        fun `주문 수량이 0 이하이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val productId = 1L
            val invalidQuantity = 0
            val unitPrice = BigDecimal(10_000L)

            // When
            val actual = assertThrows<CoreException> {
                OrderLine(
                    productId = productId,
                    quantity = invalidQuantity,
                    unitPrice = unitPrice,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("주문 수량은 0보다 커야 합니다.") },
            )
        }

        @Test
        fun `상품 단가가 음수이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val productId = 1L
            val quantity = 1
            val invalidUnitPrice = BigDecimal(-1L)

            // When
            val actual = assertThrows<CoreException> {
                OrderLine(
                    productId = productId,
                    quantity = quantity,
                    unitPrice = invalidUnitPrice,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("상품 단가는 0 이상이어야 합니다.") },
            )
        }

        @Test
        fun `올바른 상품 ID, 수량, 단가로 주문 상품을 생성하면 성공한다`() {
            // Given
            val productId = 1L
            val quantity = 2
            val unitPrice = BigDecimal(10_000L)

            // When
            val actual = OrderLine(
                productId = productId,
                quantity = quantity,
                unitPrice = unitPrice,
            )

            // Then
            assertAll(
                { assertThat(actual.productId).isEqualTo(1L) },
                { assertThat(actual.quantity).isEqualTo(2) },
                { assertThat(actual.unitPrice).isEqualTo(BigDecimal(10_000L)) },
            )
        }

        @Test
        fun `주문 상품의 수량과 단가를 곱하여 총 금액을 계산할 수 있다`() {
            // Given
            val orderLine = OrderLine(
                productId = 1L,
                quantity = 2,
                unitPrice = BigDecimal(10_000L),
            )

            // When
            val actual = orderLine.calculateLinePrice()

            // Then
            assertThat(actual).isEqualTo(BigDecimal(20_000L))
        }
    }
}
