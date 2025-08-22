package com.loopers.domain.order.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.order.model.OrderStatus
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "orders")
class Order(orderCode: String, userId: Long, orderLines: List<OrderLine>, status: OrderStatus) : BaseEntity() {

    val orderCode: String = orderCode

    val userId: Long = userId

    @OneToMany(cascade = [PERSIST])
    @JoinColumn(name = "order_id")
    val orderLines: List<OrderLine> = orderLines

    val totalPrice: BigDecimal = calculateTotalPrice()

    var status: OrderStatus = status
        private set

    var paymentAmount: BigDecimal = calculateTotalPrice()
        private set

    init {
        require(userId > 0) {
            throw CoreException(BAD_REQUEST, "유효하지 않은 사용자 ID 입니다. 사용자 ID: $userId")
        }
        require(orderLines.isNotEmpty()) {
            throw CoreException(BAD_REQUEST, "주문 항목은 비어 있을 수 없습니다.")
        }
    }

    private fun calculateTotalPrice(): BigDecimal = orderLines.sumOf { it.calculateLinePrice() }

    fun changePaymentAmount(paymentAmount: BigDecimal) {
        this.paymentAmount = paymentAmount
    }
}
