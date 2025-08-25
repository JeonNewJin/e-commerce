package com.loopers.application.payment

import com.loopers.domain.payment.model.CardType
import com.loopers.domain.payment.model.TransactionStatus

object PaymentInput {

    data class Complete(
        val transactionKey: String,
        val orderId: String,
        val cardType: CardType,
        val cardNo: String,
        val amount: Long,
        val status: TransactionStatus,
        val reason: String?,
        val paidAt: String,
    )
}
