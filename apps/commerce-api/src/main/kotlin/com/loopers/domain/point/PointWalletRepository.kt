package com.loopers.domain.point

interface PointWalletRepository {

    fun findByUserId(userId: Long): PointWallet?

    fun save(pointWallet: PointWallet): PointWallet
}
