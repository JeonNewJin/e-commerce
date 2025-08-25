package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentGateway
import com.loopers.domain.payment.model.PaymentGatewayInfo
import com.loopers.domain.payment.model.TransactionStatus
import com.loopers.infrastructure.payment.feign.PgClient
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PgSimulator(private val paymentGatewayClient: PgClient, private val paymentJpaRepository: PaymentJpaRepository) :
    PaymentGateway {

    private val logger = LoggerFactory.getLogger(PgSimulator::class.java)

    @Value("\${pg-simulator.payment.callback-url}")
    private lateinit var callbackUrl: String

    @Retry(name = "pgRetry", fallbackMethod = "fallback")
    @CircuitBreaker(name = "pgCircuit")
    override fun requestPayment(
        userId: String,
        command: PaymentCommand.Pay,
    ): PaymentGatewayInfo {
        val request = PgDto.PaymentRequest(
            orderId = command.orderCode,
            cardType = command.cardType!!,
            cardNo = command.cardNo!!,
            amount = command.amount.toLong(),
            callbackUrl = callbackUrl,
        )

        val response = paymentGatewayClient.requestPayment(userId, request)
        return response.toPaymentGatewayInfo()
    }

    override fun getPayments(
        userId: String,
        orderId: String,
    ): List<PaymentGatewayInfo> =
        paymentGatewayClient.getPayments(userId, orderId).data.transactions
            .map { it.toPaymentGatewayInfo() }

    fun fallback(
        userId: String,
        command: PaymentCommand.Pay,
        ex: Throwable,
    ): PaymentGatewayInfo {
        logger.info("PG 시뮬레이터 결제 요청 실패, 주문 코드: ${command.orderCode}, 예외: ${ex.message}")

        return PaymentGatewayInfo(
            transactionKey = "",
            status = TransactionStatus.FAILED,
            reason = "PG 시뮬레이터 결제 요청 실패: ${ex.message}",
        )
    }
}
