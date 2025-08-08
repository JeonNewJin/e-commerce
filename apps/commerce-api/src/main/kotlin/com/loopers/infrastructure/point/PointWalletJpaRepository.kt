package com.loopers.infrastructure.point

import com.loopers.domain.point.entity.PointWallet
import jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface PointWalletJpaRepository : JpaRepository<PointWallet, Long> {

    fun findByUserId(userId: Long): PointWallet?

    @Lock(PESSIMISTIC_WRITE)
    @Query("select p from PointWallet p where p.userId = :userId")
    fun findByUserIdWithLock(userId: Long): PointWallet?
}
