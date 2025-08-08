package com.loopers.domain.coupon

import com.loopers.domain.coupon.entity.Coupon
import com.loopers.domain.coupon.entity.IssuedCoupon

interface CouponRepository {

    fun save(coupon: Coupon)

    fun findById(couponId: Long): Coupon?

    fun findByIdWithLock(couponId: Long): Coupon?

    fun saveIssuedCoupon(issuedCoupon: IssuedCoupon)

    fun findIssuedCouponBy(couponId: Long, userId: Long): IssuedCoupon?
}
