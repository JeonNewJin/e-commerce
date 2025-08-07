package com.loopers.domain.point.model

import com.loopers.domain.point.entity.PointWallet
import java.math.BigDecimal

data class PointWalletInfo(val userId: Long, val balance: BigDecimal) {
    companion object {
        fun from(pointWallet: PointWallet): PointWalletInfo =
            PointWalletInfo(
                userId = pointWallet.userId,
                balance = pointWallet.balance.value,
            )
    }
}
