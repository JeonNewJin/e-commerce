package com.loopers.domain.order

import com.loopers.domain.order.entity.Order
import com.loopers.domain.order.model.OrderInfo
import com.loopers.domain.order.model.OrderStatus.PENDING
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import com.loopers.support.uuid.UUIDGenerator
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val uuidGenerator: UUIDGenerator,
    private val orderEventPublisher: OrderEventPublisher,
) {

    @Transactional
    fun placeOrder(command: OrderCommand.PlaceOrder): OrderInfo {
        val orderCode = uuidGenerator.generate()

        val order = Order(
            orderCode = orderCode,
            userId = command.userId,
            orderLines = command.orderLines,
            status = PENDING,
            couponId = command.coupon?.id,
        )

        command.coupon?.calculateDiscountedAmount(order.totalPrice)
            ?.let { order.changePaymentAmount(it) }

        orderRepository.save(order)

        return OrderInfo.from(order)
    }

    @Transactional
    fun completeOrder(orderId: Long) {
        val order = orderRepository.findById(orderId)
            ?: throw CoreException(NOT_FOUND, "주문을 찾을 수 없습니다. 주문 ID: $orderId")

        order.complete()

        orderEventPublisher.publish(OrderEvent.OrderCompleted.from(order))

        orderRepository.save(order)
    }

    @Transactional
    fun failOrder(orderId: Long) {
        val order = orderRepository.findById(orderId)
            ?: throw CoreException(NOT_FOUND, "주문을 찾을 수 없습니다. 주문 ID: $orderId")

        order.fail()
        orderRepository.save(order)
    }

    fun getOrders(command: OrderCommand.GetOrders): Page<OrderInfo> =
        orderRepository.findOrders(command)
            .map { OrderInfo.from(it) }

    fun getOrder(orderId: Long): OrderInfo =
        orderRepository.findById(orderId)
            ?.let { OrderInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "주문을 찾을 수 없습니다. 주문 ID: $orderId")

    fun getOrder(orderCode: String): OrderInfo =
        orderRepository.findByOrderCode(orderCode)
            ?.let { OrderInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "주문을 찾을 수 없습니다. 주문 코드: $orderCode")
}
