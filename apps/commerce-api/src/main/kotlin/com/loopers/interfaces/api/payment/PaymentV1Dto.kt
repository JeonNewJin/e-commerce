package com.loopers.interfaces.api.payment

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.model.CardType
import com.loopers.domain.payment.model.TransactionStatus

object PaymentV1Dto {

    class Request {

        data class Callback(
            val transactionKey: String,
            val orderId: String,
            val cardType: CardTypeDto,
            val cardNo: String,
            val amount: Long,
            val status: TransactionStatusDto,
            val reason: String?,
        ) {
            fun toCommand(paidAt: String): PaymentCommand.Complete =
                PaymentCommand.Complete(
                    orderCode = orderId,
                    transactionKey = transactionKey,
                    paidAt = paidAt,
                )
        }
    }

    enum class CardTypeDto {
        SAMSUNG,
        KB,
        HYUNDAI,
        ;

        fun toCardType(): CardType = when (this) {
            SAMSUNG -> CardType.SAMSUNG
            KB -> CardType.KB
            HYUNDAI -> CardType.HYUNDAI
        }

        companion object {
            fun from(cardType: CardType) = when (cardType) {
                CardType.SAMSUNG -> SAMSUNG
                CardType.KB -> KB
                CardType.HYUNDAI -> HYUNDAI
            }
        }
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
