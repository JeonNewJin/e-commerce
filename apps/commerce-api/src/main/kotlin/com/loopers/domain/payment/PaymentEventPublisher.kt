package com.loopers.domain.payment

interface PaymentEventPublisher {
    fun publish(event: PaymentEvent.PaymentCompleted)
}
