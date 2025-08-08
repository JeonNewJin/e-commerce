package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.CouponRepository
import com.loopers.domain.coupon.entity.Coupon
import com.loopers.domain.coupon.entity.IssuedCoupon
import org.springframework.stereotype.Component

@Component
class CouponCoreRepository(
    private val couponJpaRepository: CouponJpaRepository,
    private val issuedCouponJpaRepository: IssuedCouponJpaRepository,
) : CouponRepository {

    override fun save(coupon: Coupon) {
        couponJpaRepository.save(coupon)
    }

    override fun findById(couponId: Long): Coupon? =
        couponJpaRepository.findById(couponId).orElse(null)

    override fun findByIdWithLock(couponId: Long): Coupon? =
        couponJpaRepository.findByIdWithLock(couponId)

    override fun saveIssuedCoupon(issuedCoupon: IssuedCoupon) {
        issuedCouponJpaRepository.save(issuedCoupon)
    }

    override fun findIssuedCouponBy(couponId: Long, userId: Long): IssuedCoupon? =
        issuedCouponJpaRepository.findByCouponIdAndUserId(couponId, userId)
}
