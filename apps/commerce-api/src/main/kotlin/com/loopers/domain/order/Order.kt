package com.loopers.domain.order

import com.loopers.domain.BaseEntity
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
class Order private constructor(userId: Long, orderLines: List<OrderLine>, totalPrice: BigDecimal, status: OrderStatus) :
    BaseEntity() {

    val userId: Long = userId

    @OneToMany(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "order_id")
    val orderLines: List<OrderLine> = orderLines

    val totalPrice: BigDecimal = totalPrice

    var status: OrderStatus = status
        private set

    companion object {
        operator fun invoke(userId: Long, orderLines: List<OrderLine>, status: OrderStatus): Order {
            require(orderLines.isNotEmpty()) {
                throw CoreException(BAD_REQUEST, "주문 항목은 비어 있을 수 없습니다.")
            }

            return Order(
                userId = userId,
                orderLines = orderLines,
                totalPrice = calculateTotalPrice(orderLines),
                status = status,
            )
        }

        private fun calculateTotalPrice(orderLines: List<OrderLine>): BigDecimal = orderLines.sumOf { it.calculateLinePrice() }
    }

    fun completePayment() {
        if (status != OrderStatus.PAYMENT_PENDING) {
            throw CoreException(BAD_REQUEST, "주문 상태가 결제 대기 중이 아닙니다. 현재 상태: $status")
        }

        status = OrderStatus.PAYMENT_COMPLETED
    }
}
