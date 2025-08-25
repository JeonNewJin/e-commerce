package com.loopers.domain.payment

import com.loopers.domain.payment.model.PaymentGatewayInfo

interface PaymentGateway {

    fun requestPayment(
        userId: String,
        command: PaymentCommand.Pay,
    ): PaymentGatewayInfo

    fun getPayments(
        userId: String,
        orderId: String,
    ): List<PaymentGatewayInfo>
}
