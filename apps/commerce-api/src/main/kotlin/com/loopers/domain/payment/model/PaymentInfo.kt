package com.loopers.domain.payment.model

import com.loopers.domain.payment.PaymentMethod
import com.loopers.domain.payment.entity.Payment
import java.math.BigDecimal

data class PaymentInfo(
    val userId: Long,
    val orderCode: String,
    val paymentMethod: PaymentMethod,
    val cardType: CardType?,
    val cardNo: String?,
    val amount: BigDecimal,
    val status: PaymentStatus,
    val transactionKey: String?,
    val paidAt: String?,
    ) {
    companion object {
        fun from(payment: Payment): PaymentInfo =
            PaymentInfo(
                userId = payment.userId,
                orderCode = payment.orderCode,
                paymentMethod = payment.paymentMethod,
                cardType = payment.cardType,
                cardNo = payment.cardNo,
                amount = payment.amount,
                status = payment.status,
                transactionKey = payment.transactionKey,
                paidAt = payment.paidAt,
            )
    }
}
