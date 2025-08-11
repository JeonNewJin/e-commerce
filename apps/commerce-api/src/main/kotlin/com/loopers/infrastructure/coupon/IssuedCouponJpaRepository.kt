package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.entity.IssuedCoupon
import org.springframework.data.jpa.repository.JpaRepository

interface IssuedCouponJpaRepository : JpaRepository<IssuedCoupon, Long> {

    fun findByCouponIdAndUserId(couponId: Long, userId: Long): IssuedCoupon?

    fun findAllByCouponId(couponId: Long): List<IssuedCoupon>
}
