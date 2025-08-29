import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.domain.coupon.CouponCommand
import com.loopers.domain.coupon.CouponService
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.model.OrderInfo
import com.loopers.domain.order.model.OrderLineInfo
import com.loopers.domain.order.model.OrderStatus.PENDING
import com.loopers.domain.payment.PaymentEvent
import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.model.CardType.HYUNDAI
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.infrastructure.external.DataPlatformMockApiClient
import com.loopers.interfaces.event.payment.PaymentEventListener
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentEventListenerTest {

    private val orderService = mockk<OrderService>()
    private val stockService = mockk<StockService>(relaxed = true)
    private val couponService = mockk<CouponService>(relaxed = true)
    private val dataPlatformMockApiClient = mockk<DataPlatformMockApiClient>(relaxed = true)
    private lateinit var listener: PaymentEventListener

    @BeforeEach
    fun setUp() {
        listener = PaymentEventListener(orderService, stockService, couponService, dataPlatformMockApiClient, ObjectMapper())
        every { orderService.completeOrder(any()) } returns Unit
    }

    @Nested
    inner class `PaymentCompleted 이벤트 발생 시 ` {

        @Test
        fun `재고 차감, 쿠폰 사용, 주문 상태 변경이 호출된다`() {
            // given
            val orderId = 1L
            val orderCode = "ORDER-123"
            val userId = 10L
            val couponId = 100L
            val orderLines = listOf(OrderLineInfo(productId = 2L, quantity = 3, unitPrice = BigDecimal("10000")))
            val status = PENDING
            val totalPrice = BigDecimal("30000")
            val paymentAmount = BigDecimal("30000")
            val createdAt = "2023-10-01T12:00:00"
            val order = OrderInfo(
                id = orderId,
                orderCode = orderCode,
                userId = userId,
                couponId = couponId,
                orderLines = orderLines,
                status = status,
                totalPrice = totalPrice,
                paymentAmount = paymentAmount,
                createdAt = createdAt,
            )
            every { orderService.getOrder(orderCode) } returns order

            val paymentMethod = CARD
            val cardType = HYUNDAI
            val cardNo = "1234-1234-1234-1234"
            val transactionKey = "transactionKey"
            val event = PaymentEvent.PaymentCompleted(
                userId = userId,
                orderCode = orderCode,
                paymentMethod = paymentMethod,
                cardType = cardType,
                cardNo = cardNo,
                amount = paymentAmount,
                transactionKey = transactionKey,
            )

            // when
            listener.handle(event)

            // then
            verify {
                stockService.deduct(
                    StockCommand.Deduct(
                        productId = 2L,
                        quantity = 3,
                    ),
                )
                couponService.use(
                    CouponCommand.Use(
                        couponId = 100L,
                        userId = 10L,
                    ),
                )
                orderService.completeOrder(1L)
            }
        }

        @Test
        fun `handle와 handleDataPlatform이 순서대로 호출된다`() {
            // given
            val orderId = 1L
            val orderCode = "ORDER-123"
            val userId = 10L
            val couponId = 100L
            val orderLines = listOf(OrderLineInfo(productId = 2L, quantity = 3, unitPrice = BigDecimal("10000")))
            val status = PENDING
            val totalPrice = BigDecimal("30000")
            val paymentAmount = BigDecimal("30000")
            val createdAt = "2023-10-01T12:00:00"
            val order = OrderInfo(
                id = orderId,
                orderCode = orderCode,
                userId = userId,
                couponId = couponId,
                orderLines = orderLines,
                status = status,
                totalPrice = totalPrice,
                paymentAmount = paymentAmount,
                createdAt = createdAt,
            )
            every { orderService.getOrder(orderCode) } returns order

            val paymentMethod = CARD
            val cardType = HYUNDAI
            val cardNo = "1234-1234-1234-1234"
            val transactionKey = "transactionKey"
            val event = PaymentEvent.PaymentCompleted(
                userId = userId,
                orderCode = orderCode,
                paymentMethod = paymentMethod,
                cardType = cardType,
                cardNo = cardNo,
                amount = paymentAmount,
                transactionKey = transactionKey,
            )

            // when
            listener.handle(event)
            listener.handleDataPlatform(event)

            // then
            verifyOrder {
                stockService.deduct(any())
                couponService.use(any())
                orderService.completeOrder(any())

                dataPlatformMockApiClient.sendData(any())
            }
        }

        @Test
        fun `handle에서 예외 발생 시 handleDataPlatform은 호출되지 않는다`() {
            // given
            val orderCode = "ORDER-123"
            val event = PaymentEvent.PaymentCompleted(
                userId = 10L,
                orderCode = orderCode,
                paymentMethod = CARD,
                cardType = HYUNDAI,
                cardNo = "1234-1234-1234-1234",
                amount = BigDecimal("30000"),
                transactionKey = "transactionKey",
            )
            every { orderService.getOrder(orderCode) } throws RuntimeException("예외 발생")

            // when & then
            try {
                listener.handle(event)
                listener.handleDataPlatform(event)
            } catch (_: RuntimeException) {
                // 예외 무시
            }

            verify(exactly = 0) { dataPlatformMockApiClient.sendData(any()) }
        }
    }
}
