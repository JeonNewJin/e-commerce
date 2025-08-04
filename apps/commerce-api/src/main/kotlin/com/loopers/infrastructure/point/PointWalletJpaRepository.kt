package com.loopers.infrastructure.point

import com.loopers.domain.point.PointWallet
import org.springframework.data.jpa.repository.JpaRepository

interface PointWalletJpaRepository : JpaRepository<PointWallet, Long> {

    fun findByUserId(userId: Long): PointWallet?
}
