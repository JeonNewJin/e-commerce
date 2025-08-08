package com.loopers.domain.order

import com.loopers.domain.order.entity.OrderLine
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class OrderCommand private constructor() {

    data class PlaceOrder(val userId: Long, val orderLines: List<OrderLine>, val paymentAmount: BigDecimal)

    data class GetOrders(val userId: Long, val pageable: Pageable)
}
