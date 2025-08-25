package com.loopers.infrastructure.payment

import com.loopers.domain.payment.model.CardType
import com.loopers.domain.payment.model.PaymentGatewayInfo
import com.loopers.domain.payment.model.TransactionStatus

object PgDto {

    data class PaymentRequest(
        val orderId: String,
        val cardType: CardType,
        val cardNo: String,
        val amount: Long,
        val callbackUrl: String,
    )

    data class PaymentResponse(val meta: Meta, val data: PaymentData) {
        data class Meta(val result: String)
        data class PaymentData(val transactionKey: String, val status: TransactionStatusDto, val reason: String?)

        fun toPaymentGatewayInfo(): PaymentGatewayInfo =
            PaymentGatewayInfo(
                data.transactionKey,
                data.status.toTransactionStatus(),
                data.reason,
            )

        enum class TransactionStatusDto {
            PENDING,
            SUCCESS,
            FAILED,
            ;

            fun toTransactionStatus(): TransactionStatus = when (this) {
                PENDING -> TransactionStatus.PENDING
                SUCCESS -> TransactionStatus.SUCCESS
                FAILED -> TransactionStatus.FAILED
            }
        }
    }

    data class OrderResponse(val meta: Meta, val data: PaymentData) {
        data class Meta(val result: String)
        data class PaymentData(val orderId: String, val transactions: List<TransactionData>)
        data class TransactionData(val transactionKey: String, val status: TransactionStatusDto, val reason: String?) {
            fun toPaymentGatewayInfo(): PaymentGatewayInfo =
                PaymentGatewayInfo(
                    transactionKey,
                    status.toTransactionStatus(),
                    reason,
                )
        }

        enum class TransactionStatusDto {
            PENDING,
            SUCCESS,
            FAILED,
            ;

            fun toTransactionStatus(): TransactionStatus = when (this) {
                PENDING -> TransactionStatus.PENDING
                SUCCESS -> TransactionStatus.SUCCESS
                FAILED -> TransactionStatus.FAILED
            }
        }
    }
}
