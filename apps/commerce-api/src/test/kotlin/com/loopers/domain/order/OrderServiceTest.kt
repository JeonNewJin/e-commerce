package com.loopers.domain.order

import com.loopers.domain.order.model.OrderStatus.PAYMENT_COMPLETED
import com.loopers.domain.order.model.OrderStatus.PAYMENT_PENDING
import com.loopers.domain.order.entity.Order
import com.loopers.domain.order.entity.OrderLine
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class OrderServiceTest(
    private val orderService: OrderService,
    private val orderRepository: OrderRepository,
) : IntegrationTestSupport() {

    @Test
    fun `정상적인 주문 요청시 주문이 생성되고, 생성된 주문 요청을 반환한다`() {
        // Given
        val orderLines = listOf(
            OrderLine(
                productId = 1L,
                quantity = 2,
                unitPrice = BigDecimal(10_000L),
            ),
        )

        val command = OrderCommand.PlaceOrder(
            userId = 1L,
            orderLines = orderLines,
            paymentAmount = BigDecimal(20_000L),
        )

        // When
        val actual = orderService.placeOrder(command)

        // Then
        assertAll(
            { assertThat(actual.userId).isEqualTo(1L) },
            { assertThat(actual.orderLines).hasSize(1) },
            { assertThat(actual.orderLines[0].productId).isEqualTo(1L) },
            { assertThat(actual.orderLines[0].quantity).isEqualTo(2) },
            { assertThat(actual.status).isEqualTo(PAYMENT_PENDING) },
        )
    }

    @Test
    fun `존재하지 않는 주문의 결제 완료시 예외가 발생한다`() {
        // Given
        val nonExistentOrderId = 999L

        // When
        val actual = assertThrows<CoreException> {
            orderService.completePayment(nonExistentOrderId)
        }

        // Then
        assertAll(
            { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
            { assertThat(actual.message).isEqualTo("주문을 찾을 수 없습니다. 주문 ID: $nonExistentOrderId") },
        )
    }

    @Test
    fun `정상적인 결제 완료 요청시 주문 상태가 변경된다`() {
        // Given
        val order = Order(
            userId = 1L,
            orderLines = listOf(
                OrderLine(
                    productId = 1L,
                    quantity = 1,
                    unitPrice = BigDecimal(10_000L),
                ),
            ),
            status = PAYMENT_PENDING,
            paymentAmount = BigDecimal(10_000L),
        )
        orderRepository.save(order)

        // When
        orderService.completePayment(order.id)

        // Then
        val actual = orderRepository.findById(order.id)!!

        assertThat(actual.status).isEqualTo(PAYMENT_COMPLETED)
    }
}
