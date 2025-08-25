package com.loopers.domain.payment

import com.loopers.domain.payment.entity.Payment

interface PaymentRepository {

    fun save(payment: Payment): Payment

    fun findByOrderCode(orderCode: String): Payment?

    fun findAllByCompletedFalse(): List<Payment>
}
