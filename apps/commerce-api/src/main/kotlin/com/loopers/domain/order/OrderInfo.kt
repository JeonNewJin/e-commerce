package com.loopers.domain.order

import java.math.BigDecimal

data class OrderInfo(
    val id: Long,
    val userId: Long,
    val orderLines: List<OrderLineInfo>,
    val totalPrice: BigDecimal,
    val status: OrderStatus,
    val createdAt: String,
) {
    companion object {
        fun from(order: Order): OrderInfo =
            OrderInfo(
                id = order.id,
                userId = order.userId,
                orderLines = order.orderLines.map { OrderLineInfo.from(it) },
                totalPrice = order.totalPrice,
                status = order.status,
                createdAt = order.createdAt.toString(),
            )
    }
}
