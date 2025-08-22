package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentRepository
import com.loopers.domain.payment.entity.Payment
import org.springframework.stereotype.Component

@Component
class PaymentCoreRepository(private val paymentJpaRepository: PaymentJpaRepository) : PaymentRepository {

    override fun save(payment: Payment): Payment = paymentJpaRepository.save(payment)

    override fun findByOrderCode(orderCode: String): Payment? = paymentJpaRepository.findByOrderCode(orderCode)

    override fun findAllByCompletedFalse(): List<Payment> =
        paymentJpaRepository.findAllByCompletedFalse()
}
