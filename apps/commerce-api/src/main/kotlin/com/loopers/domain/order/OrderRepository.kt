package com.loopers.domain.order

import com.loopers.domain.order.entity.Order
import org.springframework.data.domain.Page

interface OrderRepository {

    fun save(order: Order)

    fun findById(id: Long): Order?

    fun findOrders(command: OrderCommand.GetOrders): Page<Order>
}
