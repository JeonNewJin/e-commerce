package com.loopers.infrastructure.order

import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class OrderCoreRepository(
    private val orderJpaRepository: OrderJpaRepository,
    private val customRepository: OrderCustomRepository,
) : OrderRepository {

    override fun save(order: Order) {
        orderJpaRepository.save(order)
    }

    override fun findById(id: Long): Order? = orderJpaRepository.findById(id).orElse(null)

    override fun findOrders(command: OrderCommand.GetOrders): Page<Order> =
        customRepository.findOrders(command)
}
