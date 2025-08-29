package com.loopers.domain.coupon.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.coupon.model.IssuedCouponStatus
import com.loopers.domain.coupon.model.IssuedCouponStatus.AVAILABLE
import com.loopers.domain.coupon.model.IssuedCouponStatus.USED
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "issued_coupon")
class IssuedCoupon(
    couponId: Long,
    userId: Long,
    status: IssuedCouponStatus = AVAILABLE,
    issuedAt: LocalDateTime = LocalDateTime.now(),
    usedAt: LocalDateTime? = null,
) : BaseEntity() {

    val couponId: Long = couponId

    val userId: Long = userId

    @Enumerated(STRING)
    var status: IssuedCouponStatus = status
        private set

    val issuedAt: LocalDateTime = issuedAt

    var usedAt: LocalDateTime? = usedAt
        private set

    fun use() {
        require(status == AVAILABLE) {
            throw CoreException(CONFLICT, "이미 사용된 쿠폰입니다.")
        }

        status = USED
        usedAt = LocalDateTime.now()
    }

    fun cancel() {
        require(status == USED) {
            throw CoreException(CONFLICT, "사용된 쿠폰만 취소할 수 있습니다.")
        }

        status = AVAILABLE
        usedAt = null
    }
}
