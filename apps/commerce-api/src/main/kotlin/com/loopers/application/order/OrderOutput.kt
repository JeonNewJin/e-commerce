package com.loopers.application.order

import com.loopers.domain.order.model.OrderInfo

data class OrderOutput(
    val id: Long,
    val userId: Long,
    val orderLines: List<OrderLineOutput>,
    val totalPrice: Long,
    val status: String,
    val createdAt: String,
) {
    companion object {
        fun from(order: OrderInfo): OrderOutput =
            OrderOutput(
                id = order.id,
                userId = order.userId,
                orderLines = order.orderLines.map { OrderLineOutput.from(it) },
                totalPrice = order.totalPrice.toLong(),
                status = order.status.name,
                createdAt = order.createdAt,
            )
    }
}
