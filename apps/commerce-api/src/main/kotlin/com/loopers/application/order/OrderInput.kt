package com.loopers.application.order

import com.loopers.domain.order.OrderCommand
import com.loopers.domain.payment.model.CardType
import com.loopers.domain.payment.PaymentMethod
import org.springframework.data.domain.Pageable

class OrderInput private constructor() {

    data class Order(
        val loginId: String,
        val orderItems: List<OrderItem>,
        val couponId: Long? = null,
        val paymentMethod: PaymentMethod,
        val cardType: CardType? = null,
        val cardNo: String? = null,
    ) {
        data class OrderItem(val productId: Long, val quantity: Int)
    }

    data class GetOrders(val loginId: String, val pageable: Pageable) {
        fun toCommand(userId: Long): OrderCommand.GetOrders =
            OrderCommand.GetOrders(
                userId = userId,
                pageable = pageable,
            )
    }
}
