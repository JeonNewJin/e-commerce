package com.loopers.application.order

import com.loopers.domain.order.model.OrderInfo

data class OrderOutput(
    val id: Long,
    val orderCode: String,
    val userId: Long,
    val orderLines: List<OrderLineOutput>,
    val totalPrice: Long,
    val paymentAmount: Long,
    val status: String,
    val createdAt: String,
) {
    companion object {
        fun from(order: OrderInfo): OrderOutput =
            OrderOutput(
                id = order.id,
                orderCode = order.orderCode,
                userId = order.userId,
                orderLines = order.orderLines.map { OrderLineOutput.from(it) },
                totalPrice = order.totalPrice.toLong(),
                paymentAmount = order.paymentAmount.toLong(),
                status = order.status.name,
                createdAt = order.createdAt,
            )
    }
}
