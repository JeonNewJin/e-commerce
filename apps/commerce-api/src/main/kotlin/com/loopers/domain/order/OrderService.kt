package com.loopers.domain.order

import com.loopers.domain.order.model.OrderStatus.PAYMENT_PENDING
import com.loopers.domain.order.entity.Order
import com.loopers.domain.order.model.OrderInfo
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderService(private val orderRepository: OrderRepository) {

    @Transactional
    fun placeOrder(command: OrderCommand.PlaceOrder): OrderInfo {
        val order = Order(
            userId = command.userId,
            orderLines = command.orderLines,
            status = PAYMENT_PENDING,
        )
        orderRepository.save(order)
        return OrderInfo.from(order)
    }

    @Transactional
    fun completePayment(orderId: Long) {
        val order = orderRepository.findById(orderId)
            ?: throw CoreException(NOT_FOUND, "주문을 찾을 수 없습니다. 주문 ID: $orderId")

        order.completePayment()
        orderRepository.save(order)
    }

    fun getOrders(command: OrderCommand.GetOrders): Page<OrderInfo> =
        orderRepository.findOrders(command)
            .map { OrderInfo.from(it) }

    fun getOrder(orderId: Long): OrderInfo =
        orderRepository.findById(orderId)
            ?.let { OrderInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "주문을 찾을 수 없습니다. 주문 ID: $orderId")
}
