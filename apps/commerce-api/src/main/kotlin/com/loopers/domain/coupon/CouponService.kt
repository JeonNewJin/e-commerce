package com.loopers.domain.coupon

import com.loopers.domain.coupon.entity.IssuedCoupon
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional(readOnly = true)
@Service
class CouponService(private val couponRepository: CouponRepository) {

    @Transactional
    fun issue(command: CouponCommand.Issue) {
        val coupon = couponRepository.findByIdWithLock(command.couponId)
            ?: throw CoreException(NOT_FOUND, "쿠폰을 찾을 수 없습니다.")

        coupon.issue()
        couponRepository.save(coupon)

        val issuedCoupon = IssuedCoupon(
            couponId = command.couponId,
            userId = command.userId,
        )
        couponRepository.saveIssuedCoupon(issuedCoupon)
    }

    @Transactional
    fun use(command: CouponCommand.Use) {
        val issuedCoupon = couponRepository.findIssuedCouponBy(command.couponId, command.userId)
            ?: throw CoreException(NOT_FOUND, "사용 가능한 쿠폰을 찾을 수 없습니다.")

        issuedCoupon.use()
        couponRepository.saveIssuedCoupon(issuedCoupon)
    }

    fun calculateDiscountedAmount(command: CouponCommand.CalculateDiscount): BigDecimal {
        val coupon = couponRepository.findById(command.couponId)
            ?: throw CoreException(NOT_FOUND, "쿠폰을 찾을 수 없습니다.")

        return coupon.calculateDiscountedAmount(command.orderAmount)
    }
}
