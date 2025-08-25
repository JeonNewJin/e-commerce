package com.loopers.interfaces.api.order

import com.loopers.application.order.OrderInput
import com.loopers.domain.payment.model.CardType
import com.loopers.domain.payment.PaymentMethod
import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST

object OrderV1Dto {

    class Request {

        data class Order(
            val orderItems: List<OrderItemDto>,
            val couponId: Long? = null,
            val paymentMethod: PaymentMethodDto,
            val cardType: CardTypeDto? = null,
            val cardNo: String? = null,
        ) {
            data class OrderItemDto(val productId: Long, val quantity: Int)

            init {
                if (paymentMethod == PaymentMethodDto.CARD) {
                    require(cardType != null) {
                        throw CoreException(BAD_REQUEST, "카드 결제 시 카드 타입을 지정해야 합니다.")
                    }
                    require(cardNo != null) {
                        throw CoreException(BAD_REQUEST, "카드 결제 시 카드 번호를 지정해야 합니다.")
                    }
                }
            }

            fun toInput(userId: String): OrderInput.Order = OrderInput.Order(
                loginId = userId,
                orderItems = orderItems.map { OrderInput.Order.OrderItem(it.productId, it.quantity) },
                couponId = couponId,
                paymentMethod = paymentMethod.toPaymentMethod(),
                cardType = cardType?.toCardType(),
                cardNo = cardNo,
            )
        }
    }

    enum class PaymentMethodDto {
        POINT,
        CARD,
        ;

        fun toPaymentMethod(): PaymentMethod = when (this) {
            POINT -> PaymentMethod.POINT
            CARD -> PaymentMethod.CARD
        }

        companion object {
            fun from(paymentMethod: PaymentMethod) = when (paymentMethod) {
                PaymentMethod.POINT -> POINT
                PaymentMethod.CARD -> CARD
            }
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
}
