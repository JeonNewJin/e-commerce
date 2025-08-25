package com.loopers.domain.payment.model

data class PaymentGatewayInfo(val transactionKey: String, val status: TransactionStatus, val reason: String?)

enum class TransactionStatus {
    PENDING,
    SUCCESS,
    FAILED,
}
