package com.loopers.domain.coupon

import java.math.BigDecimal

class CouponCommand private constructor() {

    data class Issue(val couponId: Long, val userId: Long)

    data class Use(val couponId: Long, val userId: Long)

    data class CalculateDiscount(val couponId: Long, val orderAmount: BigDecimal)
}
