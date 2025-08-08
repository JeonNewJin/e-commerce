package com.loopers.domain.point

import com.loopers.domain.point.entity.PointWallet
import com.loopers.domain.point.vo.Point
import com.loopers.infrastructure.point.PointWalletJpaRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class PointWalletIntegrationTest(
    private val pointWalletService: PointWalletService,
    private val pointWalletJpaRepository: PointWalletJpaRepository,
) : IntegrationTestSupport() {

    @Test
    fun `잔액이 5000 포인트일 때, 동시에 1000 포인트 충전 요청을 3번 보내면, 모든 요청이 성공하고 잔액은 8,000 포인트가 된다`() {
        // Given
        val userId = 1L
        val initialBalance = Point.of(5000L)
        val chargeAmount = Point.of(1_000L)
        pointWalletJpaRepository.save(
            PointWallet(
                userId = userId,
                balance = initialBalance,
            ),
        )

        val threadCount = 3
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) {
            executor.submit {
                try {
                    val command = PointWalletCommand.Charge(userId = userId, amount = chargeAmount.value)
                    pointWalletService.charge(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("충전 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actual = pointWalletJpaRepository.findByUserId(userId)!!

        assertThat(successCount.get()).isEqualTo(3)
        assertThat(failureCount.get()).isZero()

        assertThat(actual.balance.value).isEqualTo(BigDecimal("8000.00"))
    }

    @Test
    fun `잔액이 3000 포인트일 때, 동시에 2000 포인트 사용 요청을 3번 보내면, 1건만 성공하고 2건은 잔액 부족으로 실패해 잔액은 1000 포인트가 된다`() {
        // Given
        val userId = 1L
        val initialBalance = Point.of(3_000L)
        val useAmount = Point.of(2_000L)

        pointWalletJpaRepository.save(
            PointWallet(
                userId = userId,
                balance = initialBalance,
            ),
        )

        val threadCount = 3
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) {
            executor.submit {
                try {
                    val command = PointWalletCommand.Use(userId = userId, amount = useAmount.value)
                    pointWalletService.use(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("사용 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actual = pointWalletJpaRepository.findByUserId(userId)!!

        assertThat(successCount.get()).isEqualTo(1)
        assertThat(failureCount.get()).isEqualTo(2)

        assertThat(actual.balance.value).isEqualTo(BigDecimal("1000.00"))
    }

    @Test
    fun `잔액이 3000 포인트일 때, 동시에 1000 포인트 사용 요청을 3번 보내면, 모든 요청이 성공하고 잔액은 0 포인트가 된다`() {
        // Given
        val userId = 1L
        val initialBalance = Point.of(3_000L)
        val useAmount = Point.of(1_000L)
        pointWalletJpaRepository.save(
            PointWallet(
                userId = userId,
                balance = initialBalance,
            ),
        )

        val threadCount = 3
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) {
            executor.submit {
                try {
                    val command = PointWalletCommand.Use(userId = userId, amount = useAmount.value)
                    pointWalletService.use(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("사용 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actual = pointWalletJpaRepository.findByUserId(userId)!!

        assertThat(successCount.get()).isEqualTo(3)
        assertThat(failureCount.get()).isZero()

        assertThat(actual.balance.value).isEqualTo(BigDecimal("0.00"))
    }

    @Test
    fun `잔액이 6000 포인트일 때, 동시에 1000 포인트 충전 및 2000 포인트 사용 요청을 각각 3번씩 보내면, 모든 요청이 성공하고 잔액은 3000 포인트가 된다`() {
        // Given
        val userId = 1L
        val initialBalance = Point.of(6_000L)
        val chargeAmount = Point.of(1_000L)
        val useAmount = Point.of(2_000L)

        pointWalletJpaRepository.save(
            PointWallet(
                userId = userId,
                balance = initialBalance,
            ),
        )

        val threadCount = 6
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)

        val chargeSuccessCount = AtomicInteger(0)
        val chargeFailureCount = AtomicInteger(0)
        val useSuccessCount = AtomicInteger(0)
        val useFailureCount = AtomicInteger(0)

        // When
        repeat(threadCount) { index ->
            executor.submit {
                try {
                    if (index % 2 == 0) {
                        val command = PointWalletCommand.Charge(userId = userId, amount = chargeAmount.value)
                        pointWalletService.charge(command)
                        chargeSuccessCount.incrementAndGet()
                    } else {
                        val command = PointWalletCommand.Use(userId = userId, amount = useAmount.value)
                        pointWalletService.use(command)
                        useSuccessCount.incrementAndGet()
                    }
                } catch (e: Exception) {
                    if (index % 2 == 0) {
                        println("충전 실패: ${e.message}")
                        chargeFailureCount.incrementAndGet()
                    } else {
                        println("사용 실패: ${e.message}")
                        useFailureCount.incrementAndGet()
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actual = pointWalletJpaRepository.findByUserId(userId)!!

        assertThat(chargeSuccessCount.get()).isEqualTo(3)
        assertThat(chargeFailureCount.get()).isZero()
        assertThat(useSuccessCount.get()).isEqualTo(3)
        assertThat(useFailureCount.get()).isZero()

        assertThat(actual.balance.value).isEqualTo(BigDecimal("3000.00"))
    }
}
