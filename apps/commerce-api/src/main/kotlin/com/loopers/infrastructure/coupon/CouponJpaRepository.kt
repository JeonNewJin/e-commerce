package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.entity.Coupon
import jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface CouponJpaRepository : JpaRepository<Coupon, Long> {

    @Lock(PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :couponId")
    fun findByIdWithLock(couponId: Long): Coupon?
}
