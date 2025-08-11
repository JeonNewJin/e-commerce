package com.loopers.domain.coupon.entity

import com.loopers.domain.coupon.model.DiscountType.FIXED_AMOUNT
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class CouponTest {

    @Nested
    inner class `쿠폰을 발급할 때, ` {

        @Test
        fun `쿠폰이 소진되었으면, CONFLICT 예외가 발생한다`() {
            // given
            val coupon = Coupon(
                name = "쿠폰",
                discountType = FIXED_AMOUNT,
                discountValue = BigDecimal(3_000L),
                totalQuantity = 100,
                issuedQuantity = 100,
            )

            // when
            val actual = assertThrows<CoreException> {
                coupon.issue()
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(CONFLICT) },
                { assertThat(actual.message).isEqualTo("쿠폰이 소진되었습니다.") },
            )
        }

        @Test
        fun `쿠폰이 소진되지 않았으면, 쿠폰이 정상 발급된다`() {
            // given
            val coupon = Coupon(
                name = "쿠폰",
                discountType = FIXED_AMOUNT,
                discountValue = BigDecimal(3_000L),
                totalQuantity = 100,
                issuedQuantity = 99,
            )

            // when
            coupon.issue()

            // then
            assertThat(coupon.issuedQuantity).isEqualTo(100)
        }
    }
}
