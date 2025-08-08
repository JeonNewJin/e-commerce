package com.loopers.domain.point

import com.loopers.domain.point.entity.PointWallet

interface PointWalletRepository {

    fun findByUserId(userId: Long): PointWallet?

    fun save(pointWallet: PointWallet): PointWallet

    fun findByUserIdWithLock(userId: Long): PointWallet?
}
