package com.loopers.domain.order

import com.loopers.domain.order.entity.OrderLine
import com.loopers.domain.order.model.OrderStatus.PENDING
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.uuid.UUIDGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal

class OrderServiceTest(
    private val orderService: OrderService,
    private val orderRepository: OrderRepository,
    private val uuidGenerator: UUIDGenerator,
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
}
