package com.loopers.domain.coupon

import com.loopers.domain.coupon.entity.Coupon
import com.loopers.domain.coupon.model.DiscountType.FIXED_AMOUNT
import com.loopers.infrastructure.coupon.CouponJpaRepository
import com.loopers.infrastructure.coupon.IssuedCouponJpaRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class CouponServiceIntegrationTest(
    private val couponService: CouponService,
    private val couponJpaRepository: CouponJpaRepository,
    private val issuedCouponJpaRepository: IssuedCouponJpaRepository,
) : IntegrationTestSupport() {

    @Test
    fun `쿠폰 수량이 총 3개일 때, 동시에 5명의 사용자가 발급을 요청하면, 3명만 성공하고 2명은 실패한다`() {
        // Given
        val coupon = Coupon(
            name = "한정 쿠폰",
            discountType = FIXED_AMOUNT,
            discountValue = BigDecimal(3_000),
            totalQuantity = 3,
            issuedQuantity = 0,
        )
        couponJpaRepository.save(coupon)

        val threadCount = 5
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) { index ->
            val userId = (index + 1).toLong()
            executor.submit {
                try {
                    val command = CouponCommand.Issue(couponId = coupon.id, userId = userId)
                    couponService.issue(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("발급 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actualCoupon = couponJpaRepository.findById(coupon.id).orElseThrow()
        val actualIssuedCoupons = issuedCouponJpaRepository.findAllByCouponId(coupon.id)

        assertThat(actualCoupon.issuedQuantity).isEqualTo(3)
        assertThat(actualIssuedCoupons.size).isEqualTo(3)

        assertThat(successCount.get()).isEqualTo(3)
        assertThat(failureCount.get()).isEqualTo(2)
    }

    @Test
    fun `쿠폰 수량이 총 100개일 때, 동시에 10명의 사용자가 발급을 요청하면, 모든 요청이 성공하고 발급된 쿠폰 개수는 10개가 된다`() {
        // Given
        val coupon = Coupon(
            name = "대량 쿠폰",
            discountType = FIXED_AMOUNT,
            discountValue = BigDecimal(1_000),
            totalQuantity = 100,
            issuedQuantity = 0,
        )
        couponJpaRepository.save(coupon)

        val threadCount = 10
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) { index ->
            val userId = (index + 1).toLong()
            executor.submit {
                try {
                    val command = CouponCommand.Issue(couponId = coupon.id, userId = userId)
                    couponService.issue(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("발급 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actualCoupon = couponJpaRepository.findById(coupon.id).orElseThrow()
        val actualIssuedCoupons = issuedCouponJpaRepository.findAllByCouponId(coupon.id)

        assertThat(actualCoupon.issuedQuantity).isEqualTo(10)
        assertThat(actualIssuedCoupons.size).isEqualTo(10)

        assertThat(successCount.get()).isEqualTo(10)
        assertThat(failureCount.get()).isZero()
    }
}
