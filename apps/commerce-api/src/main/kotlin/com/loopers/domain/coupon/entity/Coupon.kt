package com.loopers.domain.coupon.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.coupon.model.DiscountType
import com.loopers.domain.coupon.model.DiscountType.FIXED_AMOUNT
import com.loopers.domain.coupon.model.DiscountType.PERCENTAGE
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "coupon")
class Coupon(
    name: String,
    discountType: DiscountType,
    discountValue: BigDecimal,
    totalQuantity: Long,
    issuedQuantity: Long = 0,
) : BaseEntity() {

    val name: String = name

    @Enumerated(STRING)
    val discountType: DiscountType = discountType

    val discountValue: BigDecimal = discountValue

    val totalQuantity: Long = totalQuantity

    var issuedQuantity: Long = issuedQuantity
        private set

    fun issue() {
        require(issuedQuantity < totalQuantity) {
            throw CoreException(CONFLICT, "쿠폰이 소진되었습니다.")
        }

        issuedQuantity++
    }

    fun calculateDiscountedAmount(orderAmount: BigDecimal): BigDecimal {
        val discountAmount = calculateDiscount(orderAmount)
        return orderAmount.subtract(discountAmount)
    }

    fun calculateDiscount(orderAmount: BigDecimal): BigDecimal {
        return when (discountType) {
            FIXED_AMOUNT -> discountValue.min(orderAmount)
            PERCENTAGE -> orderAmount.multiply(discountValue.divide(BigDecimal(100)))
        }
    }
}
