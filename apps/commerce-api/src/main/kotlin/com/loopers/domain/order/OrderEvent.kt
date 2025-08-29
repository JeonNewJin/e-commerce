package com.loopers.domain.order

import com.loopers.application.order.OrderInput
import com.loopers.domain.order.entity.Order
import com.loopers.domain.order.model.OrderInfo
import com.loopers.domain.payment.PaymentMethod
import com.loopers.domain.payment.model.CardType
import java.math.BigDecimal

object OrderEvent {

    data class OrderPlaced(
        val id: Long,
        val orderCode: String,
        val userId: Long,
        val paymentAmount: BigDecimal,
        val paymentMethod: PaymentMethod,
        val cardType: CardType?,
        val cardNo: String?,
    ) {
        companion object {
            fun from(order: OrderInfo, input: OrderInput.Order): OrderPlaced =
                OrderPlaced(
                    id = order.id,
                    orderCode = order.orderCode,
                    userId = order.userId,
                    paymentAmount = order.paymentAmount,
                    paymentMethod = input.paymentMethod,
                    cardType = input.cardType,
                    cardNo = input.cardNo,
                )
        }
    }

    data class OrderCompleted(val orderCode: String) {
        companion object {
            fun from(order: Order): OrderCompleted =
                OrderCompleted(
                    orderCode = order.orderCode,
                )
        }
    }
}
