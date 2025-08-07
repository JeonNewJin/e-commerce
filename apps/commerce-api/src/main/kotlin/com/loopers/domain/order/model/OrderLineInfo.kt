package com.loopers.domain.order.model

import com.loopers.domain.order.entity.OrderLine
import java.math.BigDecimal

data class OrderLineInfo(val productId: Long, val quantity: Int, val unitPrice: BigDecimal) {
    companion object {
        fun from(orderLine: OrderLine): OrderLineInfo =
            OrderLineInfo(
                productId = orderLine.productId,
                quantity = orderLine.quantity,
                unitPrice = orderLine.unitPrice,
            )
    }
}
