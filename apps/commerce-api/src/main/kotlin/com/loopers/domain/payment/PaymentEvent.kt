package com.loopers.domain.payment

import com.loopers.domain.payment.entity.Payment
import com.loopers.domain.payment.model.CardType
import java.math.BigDecimal

object PaymentEvent {

    data class PaymentCompleted(
        val userId: Long,
        val orderCode: String,
        val paymentMethod: PaymentMethod,
        val cardType: CardType,
        val cardNo: String?,
        val amount: BigDecimal,
        val transactionKey: String,
    ) {
        companion object {
            fun from(payment: Payment): PaymentCompleted =
                PaymentCompleted(
                userId = payment.userId,
                orderCode = payment.orderCode,
                paymentMethod = payment.paymentMethod,
                cardType = payment.cardType!!,
                cardNo = payment.cardNo,
                amount = payment.amount,
                transactionKey = payment.transactionKey!!,
            )
        }
    }
}
