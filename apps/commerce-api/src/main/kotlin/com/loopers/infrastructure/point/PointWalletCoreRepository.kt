package com.loopers.infrastructure.point

import com.loopers.domain.point.entity.PointWallet
import com.loopers.domain.point.PointWalletRepository
import org.springframework.stereotype.Component

@Component
class PointWalletCoreRepository(private val pointWalletJpaRepository: PointWalletJpaRepository) : PointWalletRepository {

    override fun findByUserId(userId: Long): PointWallet? = pointWalletJpaRepository.findByUserId(userId)

    override fun save(pointWallet: PointWallet): PointWallet = pointWalletJpaRepository.save(pointWallet)

    override fun findByUserIdWithLock(userId: Long): PointWallet? =
        pointWalletJpaRepository.findByUserIdWithLock(userId)
}
