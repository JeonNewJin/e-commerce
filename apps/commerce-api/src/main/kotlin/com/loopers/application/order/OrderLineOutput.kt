package com.loopers.application.order

import com.loopers.domain.order.OrderLineInfo
import java.math.BigDecimal

class OrderLineOutput(val productId: Long, val quantity: Int, val unitPrice: BigDecimal) {
    companion object {
        fun from(info: OrderLineInfo): OrderLineOutput =
            OrderLineOutput(
                productId = info.productId,
                quantity = info.quantity,
                unitPrice = info.unitPrice,
            )
    }
}
