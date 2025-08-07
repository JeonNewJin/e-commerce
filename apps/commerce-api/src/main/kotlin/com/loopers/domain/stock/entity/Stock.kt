package com.loopers.domain.stock.entity

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "stock")
class Stock(productId: Long, quantity: Int) : BaseEntity() {

    val productId: Long = productId

    var quantity: Int = quantity
        private set

    init {
        require(productId > 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 상품 ID 입니다. 상품 ID: $productId")
        }
        require(quantity >= 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "재고 수량은 0 이상이어야 합니다. 현재 수량: $quantity")
        }
    }

    fun canDeduct(quantity: Int): Boolean = this.quantity >= quantity

    fun deduct(quantity: Int) {
        require(quantity > 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "차감할 수량은 0보다 커야 합니다. 요청 수량: $quantity")
        }
        require(this.quantity - quantity >= 0) {
            throw CoreException(ErrorType.CONFLICT, "재고가 부족합니다. 현재 재고: ${this.quantity}, 요청 수량: $quantity")
        }

        this.quantity -= quantity
    }
}
