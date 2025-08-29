package com.loopers.domain.order

import com.loopers.domain.order.entity.Order
import com.loopers.domain.order.entity.OrderLine
import com.loopers.domain.order.model.OrderStatus.FAILED
import com.loopers.domain.order.model.OrderStatus.PENDING
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.event.ApplicationEvents
import java.math.BigDecimal

class OrderServiceTest(
    private val orderService: OrderService,
    private val orderRepository: OrderRepository,
) : IntegrationTestSupport() {

    @Autowired
    lateinit var applicationEvents: ApplicationEvents

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
        )

        // When
        val actual = orderService.placeOrder(command)

        // Then
        assertAll(
            { assertThat(actual.userId).isEqualTo(1L) },
            { assertThat(actual.orderLines).hasSize(1) },
            { assertThat(actual.orderLines[0].productId).isEqualTo(1L) },
            { assertThat(actual.orderLines[0].quantity).isEqualTo(2) },
            { assertThat(actual.status).isEqualTo(PENDING) },
        )
    }

    @Nested
    inner class `주문을 실패 처리 할 때, ` {

        @Test
        fun `실패 처리 할 주문이 존재하지 않으면, NOT_FOUND 예외가 발생한다`() {
            // given
            val nonExistentOrderId = 999L

            // when
            val actual = assertThrows<CoreException> {
                orderService.failOrder(nonExistentOrderId)
            }

            // then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("주문을 찾을 수 없습니다. 주문 ID: $nonExistentOrderId") },
            )
        }

        @Test
        fun `실패 처리 할 주문이 존재하면, 정상적으로 실패 처리가 된다`() {
            // given
            val order = Order(
                orderCode = "test-order-code",
                userId = 1L,
                status = PENDING,
                orderLines = listOf(
                    OrderLine(
                        productId = 1L,
                        quantity = 2,
                        unitPrice = BigDecimal(10_000L),
                    ),
                ),
            )
            orderRepository.save(order)

            // when
            orderService.failOrder(order.id)

            // then
            val actual = orderRepository.findById(order.id)!!
            assertThat(actual.status).isEqualTo(FAILED)
        }
    }

    @Test
    fun `주문이 완료 처리가 되면, 주문 완료 이벤트를 발행한다`() {
        // given
        val order = Order(
            orderCode = "test-order-code",
            userId = 1L,
            status = PENDING,
            orderLines = listOf(
                OrderLine(
                    productId = 1L,
                    quantity = 2,
                    unitPrice = BigDecimal(10_000L),
                ),
            ),
        )
        orderRepository.save(order)

        // when
        orderService.completeOrder(order.id)

        // then
        val eventCount = applicationEvents.stream(OrderEvent.OrderCompleted::class.java)
            .filter { it.orderCode == order.orderCode }
            .count()

        assertThat(eventCount).isEqualTo(1)
    }
}
