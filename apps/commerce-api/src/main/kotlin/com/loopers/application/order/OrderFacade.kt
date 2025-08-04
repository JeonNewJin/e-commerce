package com.loopers.application.order

import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderLine
import com.loopers.domain.order.OrderService
import com.loopers.domain.point.Point
import com.loopers.domain.point.PointWalletCommand
import com.loopers.domain.point.PointWalletService
import com.loopers.domain.product.ProductService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.LoginId
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
) {

    @Transactional
    fun placeOrder(input: OrderInput.Order) {
        val user = userService.getUser(LoginId(input.loginId))

        val orderLines = input.orderItems.map {
            val product = productService.getProduct(it.productId)
            stockService.checkAvailability(product.id, it.quantity)

            OrderLine(
                productId = product.id,
                quantity = it.quantity,
                unitPrice = product.price,
            )
        }

        val orderCommand = OrderCommand.PlaceOrder(userId = user.id, orderLines = orderLines)
        val order = orderService.placeOrder(orderCommand)

        val command = PointWalletCommand.Use(userId = user.id, amount = Point(order.totalPrice))
        pointWalletService.use(command)

        orderLines.forEach { orderLine ->
            val stockCommand = StockCommand.Deduct(productId = orderLine.productId, quantity = orderLine.quantity)
            stockService.deduct(stockCommand)
        }

        orderService.completePayment(order.id)
    }

    @Transactional(readOnly = true)
    fun getOrders(input: OrderInput.GetOrders): OrdersOutput {
        val user = userService.getUser(LoginId(input.loginId))
        val orders = orderService.getOrders(input.toCommand(user.id))
        return OrdersOutput.from(orders)
    }

    @Transactional(readOnly = true)
    fun getOrder(orderId: Long): OrderOutput =
        orderService.getOrder(orderId)
            .let { OrderOutput.from(it) }
}
