package com.loopers.interfaces.event.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.domain.coupon.CouponCommand
import com.loopers.domain.coupon.CouponService
import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentEvent
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.infrastructure.external.DataPlatformMockApiClient
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(
    private val orderService: OrderService,
    private val stockService: StockService,
    private val couponService: CouponService,
    private val dataPlatformMockApiClient: DataPlatformMockApiClient,
    private val objectMapper: ObjectMapper,
) {

    @Order(1)
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    fun handle(event: PaymentEvent.PaymentCompleted) {
        // PG 결제 취소
        // ...

        val order = orderService.getOrder(event.orderCode)

        // 재고 차감
        order.orderLines.forEach { orderLine ->
            stockService.deduct(
                StockCommand.Deduct(
                    productId = orderLine.productId,
                    quantity = orderLine.quantity,
                ),
            )
        }

        // 쿠폰 사용
        order.couponId?.let {
            couponService.use(
                CouponCommand.Use(
                    couponId = order.couponId,
                    userId = order.userId,
                ),
            )
        }

        // 주문 상태 변경
        orderService.completeOrder(order.id)
    }

    @Order(2)
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    fun handleDataPlatform(event: PaymentEvent.PaymentCompleted) {
        dataPlatformMockApiClient.sendData(objectMapper.writeValueAsString(event))
    }
}
