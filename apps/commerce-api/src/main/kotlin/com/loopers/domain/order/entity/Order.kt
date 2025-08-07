package com.loopers.domain.order.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.order.model.OrderStatus
import com.loopers.domain.order.model.OrderStatus.PAYMENT_COMPLETED
import com.loopers.domain.order.model.OrderStatus.PAYMENT_PENDING
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "orders")
class Order(userId: Long, orderLines: List<OrderLine>, status: OrderStatus) : BaseEntity() {

    val userId: Long = userId

    @OneToMany(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "order_id")
    val orderLines: List<OrderLine> = orderLines

    val totalPrice: BigDecimal = calculateTotalPrice()

    var status: OrderStatus = status
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

    fun completePayment() {
        if (status != PAYMENT_PENDING) {
            throw CoreException(BAD_REQUEST, "주문 상태가 결제 대기 중이 아닙니다. 현재 상태: $status")
        }

        status = PAYMENT_COMPLETED
    }
}
