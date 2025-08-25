package com.loopers.domain.payment.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.payment.PaymentMethod
import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.model.CardType
import com.loopers.domain.payment.model.PaymentStatus
import com.loopers.domain.payment.model.PaymentStatus.FAILED
import com.loopers.domain.payment.model.PaymentStatus.SUCCESS
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "payment")
class Payment(
    userId: Long,
    orderCode: String,
    paymentMethod: PaymentMethod,
    cardType: CardType?,
    cardNo: String?,
    amount: BigDecimal,
    status: PaymentStatus,
) : BaseEntity() {

    val userId: Long = userId

    val orderCode: String = orderCode

    @Enumerated(STRING)
    val paymentMethod: PaymentMethod = paymentMethod

    @Enumerated(STRING)
    val cardType: CardType? = cardType

    val cardNo: String? = cardNo

    val amount: BigDecimal = amount

    @Enumerated(STRING)
    var status: PaymentStatus = status
        private set

    var transactionKey: String? = null
        private set

    var paidAt: String? = null
        private set

    init {
        require(amount > BigDecimal.ZERO) {
            throw CoreException(BAD_REQUEST, "결제 금액은 0보다 커야 합니다.")
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

    fun complete(transactionKey: String, paidAt: String) {
        require(transactionKey.isNotBlank()) {
            throw CoreException(BAD_REQUEST, "거래 키는 비어있을 수 없습니다.")
        }
        require(paidAt.isNotBlank()) {
            throw CoreException(BAD_REQUEST, "결제 완료 시간은 비어있을 수 없습니다.")
        }

        this.status = SUCCESS
        this.transactionKey = transactionKey
        this.paidAt = paidAt
    }

    fun failed() {
        if (status == SUCCESS) {
            throw CoreException(BAD_REQUEST, "이미 완료된 결제는 실패 처리를 할 수 없습니다.")
        }

        this.status = FAILED
    }
}
