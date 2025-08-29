package com.loopers.interfaces.event.order

import com.loopers.domain.order.OrderEvent
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.infrastructure.external.DataPlatformMockApiClient
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderEventListener(
    private val paymentService: PaymentService,
    private val dataPlatformMockApiClient: DataPlatformMockApiClient,
) {
    private val logger = LoggerFactory.getLogger(OrderEventListener::class.java)

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handle(event: OrderEvent.OrderPlaced) {
        logger.info("Order Placed Event : {}", event)

        paymentService.pay(
            PaymentCommand.Pay(
                userId = event.userId,
                orderCode = event.orderCode,
                amount = event.paymentAmount,
                paymentMethod = event.paymentMethod,
                cardType = event.cardType,
                cardNo = event.cardNo,
            ),
        )
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handle(event: OrderEvent.OrderCompleted) {
        dataPlatformMockApiClient.sendData(event.orderCode)
    }
}
