package com.loopers.domain.coupon.entity

import com.loopers.domain.coupon.model.IssuedCouponStatus.AVAILABLE
import com.loopers.domain.coupon.model.IssuedCouponStatus.USED
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class IssuedCouponTest {

    @Nested
    inner class `쿠폰을 사용할 때, ` {
        @Test
        fun `이미 사용한 쿠폰이면, CONFLICT 예외가 발생한다`() {
            // given
            val issuedCoupon = IssuedCoupon(
                couponId = 1L,
                userId = 1L,
                status = USED,
                issuedAt = LocalDateTime.now(),
                usedAt = LocalDateTime.now().plusDays(1),
            )

            // when
            val actual = assertThrows<CoreException> {
                issuedCoupon.use()
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(CONFLICT) },
                { assertThat(actual.message).isEqualTo("이미 사용된 쿠폰입니다.") },
            )
        }

        @Test
        fun `사용하지 않은 쿠폰이면, 쿠폰이 정상 사용된다`() {
            // given
            val issuedCoupon = IssuedCoupon(
                couponId = 1L,
                userId = 1L,
                status = AVAILABLE,
                issuedAt = LocalDateTime.now(),
            )

            // when
            issuedCoupon.use()

            // then
            assertAll(
                { assertThat(issuedCoupon.status).isEqualTo(USED) },
                { assertThat(issuedCoupon.usedAt).isNotNull() },
            )
        }
    }

    @Nested
    inner class `쿠폰 사용을 취소할 때, ` {

        @Test
        fun `쿠폰 상태가 USED가 아니면, CONFLICT 예외가 발생한다`() {
            // given
            val issuedCoupon = IssuedCoupon(
                couponId = 1L,
                userId = 1L,
                status = AVAILABLE,
                issuedAt = LocalDateTime.now(),
            )

            // when
            val actual = assertThrows<CoreException> {
                issuedCoupon.cancel()
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(CONFLICT) },
                { assertThat(actual.message).isEqualTo("사용된 쿠폰만 취소할 수 있습니다.") },
            )
        }

        @Test
        fun `쿠폰 상태가 USED이면, 정상적으로 취소된다`() {
            // given
            val issuedCoupon = IssuedCoupon(
                couponId = 1L,
                userId = 1L,
                status = USED,
                issuedAt = LocalDateTime.now(),
                usedAt = LocalDateTime.now().plusDays(1),
            )

            // when
            issuedCoupon.cancel()

            // then
            assertAll(
                { assertThat(issuedCoupon.status).isEqualTo(AVAILABLE) },
                { assertThat(issuedCoupon.usedAt).isNull() },
            )
        }
    }
}
