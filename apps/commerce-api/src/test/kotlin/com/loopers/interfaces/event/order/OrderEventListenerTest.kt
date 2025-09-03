import com.loopers.domain.order.OrderEvent
import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.model.CardType.HYUNDAI
import com.loopers.infrastructure.external.DataPlatformMockApiClient
import com.loopers.interfaces.event.order.OrderEventListener
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderEventListenerTest {

    private val paymentService = mockk<PaymentService>(relaxed = true)
    private val dataPlatformMockApiClient = mockk<DataPlatformMockApiClient>(relaxed = true)
    private lateinit var listener: OrderEventListener

    @BeforeEach
    fun setUp() {
        listener = OrderEventListener(paymentService, dataPlatformMockApiClient)
    }

    @Test
    fun `OrderCreated 이벤트 발생 시 결제가 호출된다`() {
        // given
        val event = OrderEvent.OrderPlaced(
            id = 1L,
            orderCode = "ORDER-123",
            userId = 10L,
            paymentAmount = BigDecimal("30000"),
            paymentMethod = CARD,
            cardType = HYUNDAI,
            cardNo = "1234-5678-9012-3456",
        )

        // when
        listener.handle(event)

        // then
        verify {
            paymentService.pay(
                match {
                    it.userId == 10L &&
                            it.orderCode == "ORDER-123" &&
                            it.amount == BigDecimal("30000") &&
                            it.paymentMethod == CARD &&
                            it.cardType == HYUNDAI &&
                            it.cardNo == "1234-5678-9012-3456"
                },
            )
        }
    }

    @Test
    fun `OrderCompleted 이벤트 발생 시 데이터 플랫폼에 데이터 전송이 호출된다`() {
        // given
        val event = OrderEvent.OrderCompleted(
            orderCode = "ORDER-123",
        )

        // when
        listener.handle(event)

        // then
        verify { dataPlatformMockApiClient.sendData(event.orderCode) }
    }
}
