package com.loopers.domain.payment

interface PaymentProcessor {

    fun paymentMethod(): PaymentMethod

    fun process(command: PaymentCommand.Pay)
}
