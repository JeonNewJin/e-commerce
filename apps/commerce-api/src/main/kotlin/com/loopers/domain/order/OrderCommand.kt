package com.loopers.domain.order

import org.springframework.data.domain.Pageable

class OrderCommand private constructor() {

    data class PlaceOrder(val userId: Long, val orderLines: List<OrderLine>)

    data class GetOrders(val userId: Long, val pageable: Pageable)
}
