package com.loopers.application.order

import com.loopers.domain.coupon.CouponService
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderEvent
import com.loopers.domain.order.OrderEventPublisher
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.entity.OrderLine
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val orderService: OrderService,
    private val couponService: CouponService,
    private val orderEventPublisher: OrderEventPublisher,
) {

    @Transactional
    fun placeOrder(input: OrderInput.Order): OrderOutput {
        val user = userService.getUser(input.loginId)

        val orderLines = input.orderItems.map {
            val product = productService.getProductOnSale(it.productId)
            OrderLine(productId = product.id, quantity = it.quantity, unitPrice = product.price)
        }

        val coupon = input.couponId?.let { couponService.findCouponIfIssuedToUser(couponId = input.couponId, userId = user.id) }
        val order = orderService.placeOrder(
            OrderCommand.PlaceOrder(
                userId = user.id,
                orderLines = orderLines,
                coupon = coupon,
            ),
        )

        orderEventPublisher.publish(OrderEvent.OrderPlaced.from(order, input))

        return OrderOutput.from(order)
    }

    @Transactional(readOnly = true)
    fun getOrders(input: OrderInput.GetOrders): OrdersOutput {
        val user = userService.getUser(input.loginId)
        val orders = orderService.getOrders(input.toCommand(user.id))
        return OrdersOutput.from(orders)
    }

    @Transactional(readOnly = true)
    fun getOrder(orderId: Long): OrderOutput =
        orderService.getOrder(orderId)
            .let { OrderOutput.from(it) }
}
