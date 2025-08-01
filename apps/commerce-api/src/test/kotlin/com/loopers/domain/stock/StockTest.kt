package com.loopers.domain.stock

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import com.loopers.support.error.ErrorType.CONFLICT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StockTest {

    @Nested
    inner class `재고를 생성할 때, ` {

        @Test
        fun `음수로 재고를 생성하면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val productId = 1L
            val negativeQuantity = -1

            // When
            val actual = assertThrows<CoreException> {
                Stock(productId, negativeQuantity)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).contains("재고 수량은 0 이상이어야 합니다.") },
            )
        }

        @Test
        fun `0 이상의 재고로 생성하면, 정상 생성된다`() {
            // Given
            val productId = 1L
            val quantity = 0

            // When
            val actual = Stock(productId, quantity)

            // Then
            assertAll(
                { assertThat(actual.productId).isEqualTo(1L) },
                { assertThat(actual.quantity).isEqualTo(0) },
            )
        }
    }

    @Nested
    inner class `재고를 차감할 때, ` {

        @Test
        fun `0보다 작은 수량을 차감하면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val stock = Stock(1L, 10)
            val negativeQuantity = -1

            // When
            val actual = assertThrows<CoreException> {
                stock.deduct(negativeQuantity)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).contains("차감할 수량은 0보다 커야 합니다.") },
            )
        }

        @Test
        fun `재고보다 많은 수량을 차감하면, CONFLICT 예외가 발생한다`() {
            // Given
            val stock = Stock(1L, 10)
            val excessQuantity = 11

            // When
            val actual = assertThrows<CoreException> {
                stock.deduct(excessQuantity)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(CONFLICT) },
                { assertThat(actual.message).contains("재고가 부족합니다. 현재 재고: 10, 요청 수량: 11") },
            )
        }

        @Test
        fun `재고에서 정상적으로 차감하면, 재고가 감소한다`() {
            // Given
            val stock = Stock(1L, 10)
            val quantityToDeduct = 5

            // When
            stock.deduct(quantityToDeduct)

            // Then
            assertThat(stock.quantity).isEqualTo(5)
        }
    }
}
