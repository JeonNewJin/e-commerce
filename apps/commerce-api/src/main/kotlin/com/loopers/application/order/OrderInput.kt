package com.loopers.application.order

import com.loopers.domain.order.OrderCommand
import org.springframework.data.domain.Pageable

class OrderInput private constructor() {

    data class Order(val loginId: String, val orderItems: List<OrderItem>)

    data class OrderItem(val productId: Long, val quantity: Int)

    data class GetOrders(val loginId: String, val pageable: Pageable) {
        fun toCommand(userId: Long): OrderCommand.GetOrders =
            OrderCommand.GetOrders(
                userId = userId,
                pageable = pageable,
            )
    }
}
