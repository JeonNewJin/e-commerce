package com.loopers.domain.coupon

import com.loopers.domain.coupon.entity.Coupon
import com.loopers.domain.coupon.entity.IssuedCoupon
import com.loopers.domain.coupon.model.DiscountType.FIXED_AMOUNT
import com.loopers.domain.coupon.model.DiscountType.PERCENTAGE
import com.loopers.domain.coupon.model.IssuedCouponStatus.AVAILABLE
import com.loopers.domain.coupon.model.IssuedCouponStatus.USED
import com.loopers.infrastructure.coupon.CouponJpaRepository
import com.loopers.infrastructure.coupon.IssuedCouponJpaRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class CouponServiceTest(
    private val couponService: CouponService,
    private val couponJpaRepository: CouponJpaRepository,
    private val issuedCouponJpaRepository: IssuedCouponJpaRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `쿠폰을 발급할 때, ` {

        @Test
        fun `존재하지 않는 쿠폰 ID로 요청하면, NOT_FOUND 예외가 발생한다`() {
            // given
            val nonExistentCouponId = 999L

            val command = CouponCommand.Issue(
                couponId = nonExistentCouponId,
                userId = 1L,
            )

            // when
            val actual = assertThrows<CoreException> {
                couponService.issue(command)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("쿠폰을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `존재하는 쿠폰 ID로 요청하면, 쿠폰이 정상 발급된다`() {
            // given
            val coupon = Coupon(
                name = "쿠폰",
                discountType = FIXED_AMOUNT,
                discountValue = BigDecimal(3_000L),
                totalQuantity = 100,
                issuedQuantity = 99,
            )
            couponJpaRepository.save(coupon)

            val couponId = 1L
            val userId = 1L
            val command = CouponCommand.Issue(couponId = couponId, userId = userId)

            // when
            couponService.issue(command)

            // then
            val actual = issuedCouponJpaRepository.findByCouponIdAndUserId(couponId, userId)!!

            assertAll(
                { assertThat(actual.couponId).isEqualTo(1L) },
                { assertThat(actual.userId).isEqualTo(1L) },
                { assertThat(actual.status).isEqualTo(AVAILABLE) },
            )
        }
    }

    @Nested
    inner class `쿠폰을 사용할 때, ` {

        @Test
        fun `존재하지 않는 쿠폰 ID로 요청하면, NOT_FOUND 예외가 발생한다`() {
            // given
            val nonExistentCouponId = 999L
            val userId = 1L

            val command = CouponCommand.Use(
                couponId = nonExistentCouponId,
                userId = userId,
            )

            // when
            val actual = assertThrows<CoreException> {
                couponService.use(command)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("사용 가능한 쿠폰을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `발급된 쿠폰을 사용하면, 쿠폰 상태가 사용됨으로 변경된다`() {
            // given
            val coupon = Coupon(
                name = "쿠폰",
                discountType = FIXED_AMOUNT,
                discountValue = BigDecimal(3_000L),
                totalQuantity = 100,
                issuedQuantity = 0,
            )
            couponJpaRepository.save(coupon)

            val couponId = 1L
            val userId = 1L

            couponService.issue(CouponCommand.Issue(couponId, userId))

            val command = CouponCommand.Use(
                couponId = couponId,
                userId = userId,
            )

            // when
            couponService.use(command)

            // then
            val actual = issuedCouponJpaRepository.findByCouponIdAndUserId(couponId, userId)!!

            assertThat(actual.status).isEqualTo(USED)
        }
    }

    @Nested
    inner class `쿠폰을 사용을 취소할 때, ` {

        @Test
        fun `취소 가능한 쿠폰이 존재하지 않으면, NOT_FOUND 예외가 발생한다`() {
            // given
            val nonExistentCouponId = 999L
            val userId = 1L

            val command = CouponCommand.Cancel(
                couponId = nonExistentCouponId,
                userId = userId,
            )

            // when
            val actual = assertThrows<CoreException> {
                couponService.cancelCouponUsage(command)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("취소 가능한 쿠폰을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `취소 가능한 쿠폰이 존재하면, 정상적으로 사용 취소된다`() {
            // given
            val couponId = 1L
            val userId = 2L
            val issuedCoupon = IssuedCoupon(
                couponId = couponId,
                userId = userId,
                status = USED,
                issuedAt = java.time.LocalDateTime.now(),
                usedAt = java.time.LocalDateTime.now(),
            )
            issuedCouponJpaRepository.save(issuedCoupon)

            val command = CouponCommand.Cancel(couponId = couponId, userId = userId)

            // when
            couponService.cancelCouponUsage(command)

            // then
            val actual = issuedCouponJpaRepository.findByCouponIdAndUserId(couponId, userId)!!
            assertAll(
                { assertThat(actual.status).isEqualTo(AVAILABLE) },
                { assertThat(actual.usedAt).isNull() },
            )
        }
    }

    @Nested
    inner class `할인 금액을 계산할 때, ` {

        @Test
        fun `존재하지 않는 쿠폰으로 계산 시도하면, NOT_FOUND 예외가 발생한다`() {
            // given
            val nonExistentCouponId = 999L
            val orderAmount = BigDecimal(10_000)

            val command = CouponCommand.CalculateDiscount(
                couponId = nonExistentCouponId,
                orderAmount = orderAmount,
            )

            // when
            val actual = assertThrows<CoreException> {
                couponService.calculateDiscountedAmount(command)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("쿠폰을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `정액 할인 쿠폰으로 계산하면, 주문 금액에서 할인 금액을 뺀 값을 반환한다`() {
            // given
            val discountAmount = BigDecimal(3_000)
            val coupon = Coupon(
                name = "3천원 할인 쿠폰",
                discountType = FIXED_AMOUNT,
                discountValue = discountAmount,
                totalQuantity = 100,
                issuedQuantity = 0,
            )
            couponJpaRepository.save(coupon)

            val orderAmount = BigDecimal(10_000)

            val command = CouponCommand.CalculateDiscount(
                couponId = 1L,
                orderAmount = orderAmount,
            )

            // when
            val actual = couponService.calculateDiscountedAmount(command)

            // then
            assertThat(actual).isEqualTo(BigDecimal("7000.00"))
        }

        @Test
        fun `정률 할인 쿠폰으로 계산하면, 주문 금액에서 할인율을 적용한 금액을 반환한다`() {
            // given
            val discountRate = BigDecimal(10)
            val coupon = Coupon(
                name = "10% 할인 쿠폰",
                discountType = PERCENTAGE,
                discountValue = discountRate,
                totalQuantity = 100,
                issuedQuantity = 0,
            )
            couponJpaRepository.save(coupon)

            val orderAmount = BigDecimal(10_000)

            val command = CouponCommand.CalculateDiscount(
                couponId = 1L,
                orderAmount = orderAmount,
            )

            // when
            val actual = couponService.calculateDiscountedAmount(command)

            // then
            assertThat(actual).isEqualTo(BigDecimal("9000.00"))
        }
    }
}
