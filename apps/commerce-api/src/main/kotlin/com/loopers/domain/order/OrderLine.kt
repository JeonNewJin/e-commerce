package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_line")
class OrderLine private constructor(productId: Long, quantity: Int, unitPrice: BigDecimal) : BaseEntity() {

    val productId: Long = productId
    val quantity: Int = quantity
    val unitPrice: BigDecimal = unitPrice

    companion object {
        operator fun invoke(productId: Long, quantity: Int, unitPrice: BigDecimal): OrderLine {
            require(quantity > 0) {
                throw CoreException(BAD_REQUEST, "주문 수량은 0보다 커야 합니다.")
            }
            require(unitPrice >= BigDecimal.ZERO) {
                throw CoreException(BAD_REQUEST, "상품 단가는 0 이상이어야 합니다.")
            }

            return OrderLine(
                productId = productId,
                quantity = quantity,
                unitPrice = unitPrice,
            )
        }
    }

    fun calculateLinePrice(): BigDecimal = unitPrice.multiply(BigDecimal(quantity))
}
