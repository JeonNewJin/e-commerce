package com.loopers.domain.order

import org.springframework.data.domain.Page

interface OrderRepository {

    fun save(order: Order)

    fun findById(id: Long): Order?

    fun findOrders(command: OrderCommand.GetOrders): Page<Order>
}
