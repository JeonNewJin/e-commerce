package com.loopers.domain.stock

import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

class StockServiceTest(
    private val stockService: StockService,
    private val stockRepository: StockRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `재고를 차감할 때,` {

        @Test
        fun `재고가 존재하지 않는 상품 ID로 차감을 시도하면, NOT_FOUND 예외가 발생한다`() {
            // Given
            val nonExistentProductId = 999L // 예시로 존재하지 않는 상품 ID
            val command = StockCommand.Deduct(
                productId = nonExistentProductId,
                quantity = 1,
            )

            // When
            val actual = assertThrows<CoreException> {
                stockService.deduct(command)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).contains("해당 상품의 재고를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `정상적인 재고 차감 요청시 재고가 감소한다`() {
            // Given
            val productId = 1L
            val initialQuantity = 10
            val deductQuantity = 3

            val stock = Stock(
                productId = productId,
                quantity = initialQuantity,
            )
            stockRepository.save(stock)

            val command = StockCommand.Deduct(
                productId = productId,
                quantity = deductQuantity,
            )

            // When
            stockService.deduct(command)

            // Then
            val actual = stockRepository.findByProductId(productId)!!

            assertThat(actual.quantity).isEqualTo(7)
        }
    }

    @Test
    fun `재고 차감 가능 여부를 확인할 때, 주문 수량보다 재고가 부족하면, CONFLICT 예외가 발생된다`() {
        // Given
        val productId = 1L
        val initialQuantity = 5
        val orderQuantity = 10

        val stock = Stock(
            productId = productId,
            quantity = initialQuantity,
        )
        stockRepository.save(stock)

        // When
        val actual = assertThrows<CoreException> {
            stockService.checkAvailability(productId, orderQuantity)
        }

        // Then
        assertAll(
            { assertThat(actual.errorType).isEqualTo(CONFLICT) },
            { assertThat(actual.message).contains("재고가 부족합니다. 현재 재고: $initialQuantity, 요청 수량: $orderQuantity") },
        )
    }
}
