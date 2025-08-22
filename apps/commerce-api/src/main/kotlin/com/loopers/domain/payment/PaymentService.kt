package com.loopers.domain.payment

import com.loopers.domain.payment.model.PaymentGatewayInfo
import com.loopers.domain.payment.model.PaymentInfo
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PaymentService(
    paymentProcessors: List<PaymentProcessor>,
    private val paymentRepository: PaymentRepository,
    private val paymentGateway: PaymentGateway,
) {

    private val paymentProcessorMap by lazy { paymentProcessors.associateBy { it.paymentMethod() } }

    @Transactional
    fun pay(command: PaymentCommand.Pay) {
        val processor = paymentProcessorMap[command.paymentMethod]
            ?: throw CoreException(BAD_REQUEST, "지원하지 않는 결제 수단입니다: ${command.paymentMethod}")

        processor.process(command)
    }

    @Transactional
    fun complete(command: PaymentCommand.Complete) {
        val payment = paymentRepository.findByOrderCode(command.orderCode)
            ?: throw CoreException(NOT_FOUND, "결제 정보를 찾을 수 없습니다. 주문 코드: ${command.orderCode}")

        payment.complete(command.transactionKey, command.paidAt)
        paymentRepository.save(payment)
    }

    fun getNotCompletedPayments(): List<PaymentInfo> =
        paymentRepository.findAllByCompletedFalse()
            .map { PaymentInfo.from(it) }

    fun getPaymentByOrderCode(userId: String, orderCode: String): List<PaymentGatewayInfo> =
        paymentGateway.getPayments(userId, orderCode)
}
