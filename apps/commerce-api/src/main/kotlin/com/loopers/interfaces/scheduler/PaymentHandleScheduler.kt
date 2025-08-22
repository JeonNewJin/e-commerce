package com.loopers.interfaces.scheduler

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.model.TransactionStatus.SUCCESS
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PaymentHandleScheduler(private val paymentService: PaymentService) {

    @Scheduled(fixedDelay = 60000)
    fun handlePayment() {
        val payments = paymentService.getNotCompletedPayments()
        payments.forEach { payment ->
            val findPayment = paymentService.getPaymentByOrderCode(
                userId = payment.userId.toString(),
                orderCode = payment.orderCode,
            ).first()

            if (findPayment.status == SUCCESS) {
                paymentService.complete(
                    command = PaymentCommand.Complete(
                        orderCode = payment.orderCode,
                        transactionKey = findPayment.transactionKey,
                        paidAt = LocalDateTime.now().toString(),
                    ),
                )
            }
        }
    }
}
