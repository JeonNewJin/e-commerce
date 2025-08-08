package com.loopers.domain.stock

import com.loopers.domain.stock.entity.Stock
import com.loopers.infrastructure.stock.StockJpaRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class StockServiceIntegrationTest(
    private val stockService: StockService,
    private val stockJpaRepository: StockJpaRepository,
) : IntegrationTestSupport() {

    @Test
    fun `재고가 6개 있을 때, 동시에 서로 다른 수량을 차감하는 여러 요청이 들어오면 정확하게 차감된다`() {
        // Given
        val productId = 1L
        val initialQuantity = 6

        val stock = Stock(
            productId = productId,
            quantity = initialQuantity,
        )
        stockJpaRepository.save(stock)

        val deductQuantities = listOf(1, 2, 3)
        val threadCount = deductQuantities.size
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        deductQuantities.forEach { quantity ->
            executor.submit {
                try {
                    val command = StockCommand.Deduct(productId = productId, quantity = quantity)
                    stockService.deduct(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("재고 차감 실패 (수량: $quantity): ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actual = stockJpaRepository.findByProductId(productId)!!

        assertThat(successCount.get()).isEqualTo(3)
        assertThat(failureCount.get()).isZero()

        assertThat(actual.quantity).isEqualTo(0)
    }

    @Test
    fun `재고가 3개가 있을 때, 동시에 재고 차감 요청을 3번 보내면, 모든 요청이 성공하고 재고는 0개가 된다`() {
        // Given
        val productId = 1L
        val initialQuantity = 3
        val deductQuantity = 1

        val stock = Stock(
            productId = productId,
            quantity = initialQuantity,
        )
        stockJpaRepository.save(stock)

        val threadCount = 3
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) {
            executor.submit {
                try {
                    val command = StockCommand.Deduct(productId = productId, quantity = deductQuantity)
                    stockService.deduct(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("재고 차감 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actual = stockJpaRepository.findByProductId(productId)!!

        assertThat(successCount.get()).isEqualTo(3)
        assertThat(failureCount.get()).isZero()

        assertThat(actual.quantity).isEqualTo(0)
    }

    @Test
    fun `재고가 2개가 있을 때, 동시에 재고 차감 요청을 3번 보내면, 2건은 성공하고 1건은 실패해 재고는 0개가 된다`() {
        // Given
        val productId = 1L
        val initialQuantity = 2
        val deductQuantity = 1

        val stock = Stock(
            productId = productId,
            quantity = initialQuantity,
        )
        stockJpaRepository.save(stock)

        val threadCount = 3
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) {
            executor.submit {
                try {
                    val command = StockCommand.Deduct(productId = productId, quantity = deductQuantity)
                    stockService.deduct(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("재고 차감 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actual = stockJpaRepository.findByProductId(productId)!!

        assertThat(successCount.get()).isEqualTo(2)
        assertThat(failureCount.get()).isEqualTo(1)

        assertThat(actual.quantity).isEqualTo(0)
    }
}
