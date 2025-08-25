package com.loopers.domain.order

import com.loopers.domain.coupon.entity.Coupon
import com.loopers.domain.order.entity.OrderLine
import org.springframework.data.domain.Pageable

class OrderCommand private constructor() {

    data class PlaceOrder(val userId: Long, val orderLines: List<OrderLine>, val coupon: Coupon? = null)

    data class GetOrders(val userId: Long, val pageable: Pageable)
}
