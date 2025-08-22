package com.loopers.domain.payment

import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.entity.Payment
import com.loopers.domain.payment.model.PaymentStatus.PENDING
import com.loopers.domain.payment.model.TransactionStatus.FAILED
import com.loopers.infrastructure.support.transaction.TransactionSynchronizationExecutor
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class CardPaymentProcessor(
    private val paymentRepository: PaymentRepository,
    private val transactionSynchronizationExecutor: TransactionSynchronizationExecutor,
    private val paymentGateway: PaymentGateway,
) : PaymentProcessor {

    override fun paymentMethod(): PaymentMethod = CARD

    @Transactional
    override fun process(command: PaymentCommand.Pay) {
        val payment = Payment(
            userId = command.userId,
            orderCode = command.orderCode,
            amount = command.amount,
            paymentMethod = command.paymentMethod,
            cardType = command.cardType,
            cardNo = command.cardNo,
            status = PENDING,
        )
        paymentRepository.save(payment)

        transactionSynchronizationExecutor.afterCommit {
            val result = paymentGateway.requestPayment(command.userId.toString(), command)

            if (result.status == FAILED) {
                payment.failed()
                paymentRepository.save(payment)
            }
        }
    }
}
