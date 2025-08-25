package com.loopers.application.payment

import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.infrastructure.support.transaction.TransactionSynchronizationExecutor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentFacade(
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val stockService: StockService,
    private val transactionSynchronizationExecutor: TransactionSynchronizationExecutor,
) {

    @Transactional
    fun complete(input: PaymentInput.Complete) {
        paymentService.complete(
            PaymentCommand.Complete(
                orderCode = input.orderId,
                transactionKey = input.transactionKey,
                paidAt = input.paidAt,
            ),
        )

        transactionSynchronizationExecutor.afterCommit {
            val order = orderService.getOrder(input.orderId)
            try {
                order.orderLines.forEach { orderLine ->
                    stockService.deduct(
                        StockCommand.Deduct(
                            productId = orderLine.productId,
                            quantity = orderLine.quantity,
                        ),
                    )
                }
            } catch (e: Exception) {
                // 주문 완료 후 재고 차감 중 오류 발생 시, 결제 취소
                // 쿠폰 사용 취소 등 추가 작업이 필요할 수 있음
                throw e
            }
        }
    }
}
