package com.loopers.application.order

import com.loopers.domain.coupon.CouponCommand
import com.loopers.domain.coupon.CouponService
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.entity.OrderLine
import com.loopers.domain.point.PointWalletCommand
import com.loopers.domain.point.PointWalletService
import com.loopers.domain.product.ProductService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val stockService: StockService,
    private val orderService: OrderService,
    private val pointWalletService: PointWalletService,
    private val couponService: CouponService,
) {

    @Transactional
    fun placeOrder(input: OrderInput.Order) {
        val user = userService.getUser(input.loginId)

        val orderLines = input.orderItems.map {
            val product = productService.getProductOnSale(it.productId)
            OrderLine(productId = product.id, quantity = it.quantity, unitPrice = product.price)
        }

        var paymentAmount = orderLines.sumOf { it.calculateLinePrice() }
        if (input.couponId != null) {
            couponService.use(CouponCommand.Use(couponId = input.couponId, userId = user.id))
            paymentAmount = couponService.calculateDiscountedAmount(
                CouponCommand.CalculateDiscount(
                    couponId = input.couponId,
                    orderAmount = paymentAmount,
                ),
            )
        }

        val order = orderService.placeOrder(
            OrderCommand.PlaceOrder(
                userId = user.id,
                orderLines = orderLines,
                paymentAmount = paymentAmount,
            ),
        )

        pointWalletService.use(PointWalletCommand.Use(userId = user.id, amount = order.paymentAmount))

        orderService.completePayment(order.id)

        orderLines.forEach { orderLine ->
            val stockCommand = StockCommand.Deduct(productId = orderLine.productId, quantity = orderLine.quantity)
            stockService.deduct(stockCommand)
        }
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
