package com.loopers.application.point

import com.loopers.domain.point.model.PointWalletInfo
import java.math.BigDecimal

data class PointWalletOutput(val userId: Long, val balance: BigDecimal) {
    companion object {
        fun from(info: PointWalletInfo): PointWalletOutput =
            PointWalletOutput(
                userId = info.userId,
                balance = info.balance,
            )
    }
}
