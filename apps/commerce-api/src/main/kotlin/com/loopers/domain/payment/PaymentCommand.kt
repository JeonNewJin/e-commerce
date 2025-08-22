package com.loopers.domain.payment

import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.model.CardType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import java.math.BigDecimal

object PaymentCommand {

    data class Pay(
        val userId: Long,
        val orderCode: String,
        val amount: BigDecimal,
        val paymentMethod: PaymentMethod,
        val cardType: CardType? = null,
        val cardNo: String? = null,
    ) {
        init {
            require(orderCode.isNotBlank()) {
                throw CoreException(BAD_REQUEST, "주문 코드는 비어있을 수 없습니다.")
            }
            if (paymentMethod == CARD) {
                require(cardType != null) {
                    throw CoreException(BAD_REQUEST, "카드 결제 시 카드 타입을 지정해야 합니다.")
                }
                require(cardNo != null) {
                    throw CoreException(BAD_REQUEST, "카드 결제 시 카드 번호를 지정해야 합니다.")
                }
            }
        }
    }

    data class Complete(val orderCode: String, val transactionKey: String, val paidAt: String) {
        init {
            require(orderCode.isNotBlank()) {
                throw CoreException(BAD_REQUEST, "주문 코드는 비어있을 수 없습니다.")
            }
            require(transactionKey.isNotBlank()) {
                throw CoreException(BAD_REQUEST, "거래 키는 비어있을 수 없습니다.")
            }
            require(paidAt.isNotBlank()) {
                throw CoreException(BAD_REQUEST, "결제 완료 시간은 비어있을 수 없습니다.")
            }
        }
    }
}
