package com.loopers.domain.stock

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import com.loopers.support.error.ErrorType.CONFLICT
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "stock")
class Stock private constructor(productId: Long, quantity: Int) : BaseEntity() {

    val productId: Long = productId

    var quantity: Int = quantity
        private set

    companion object {
        operator fun invoke(productId: Long, quantity: Int = 0): Stock {
            require(quantity >= 0) {
                throw CoreException(BAD_REQUEST, "재고 수량은 0 이상이어야 합니다.")
            }

            return Stock(productId = productId, quantity = quantity)
        }
    }

    fun canDeduct(quantity: Int): Boolean = this.quantity >= quantity

    fun deduct(quantity: Int) {
        require(quantity > 0) {
            throw CoreException(BAD_REQUEST, "차감할 수량은 0보다 커야 합니다.")
        }
        require(this.quantity - quantity >= 0) {
            throw CoreException(CONFLICT, "재고가 부족합니다. 현재 재고: ${this.quantity}, 요청 수량: $quantity")
        }

        this.quantity -= quantity
    }
}
