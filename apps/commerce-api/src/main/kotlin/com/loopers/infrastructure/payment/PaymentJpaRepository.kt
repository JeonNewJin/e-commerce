package com.loopers.infrastructure.payment

import com.loopers.domain.payment.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PaymentJpaRepository : JpaRepository<Payment, Long> {

    fun findByOrderCode(orderCode: String): Payment?

    @Query("select p from Payment p where p.status != 'SUCCESS'")
    fun findAllByCompletedFalse(): List<Payment>
}
