package com.loopers.application.point

import com.loopers.domain.point.PointWalletCommand
import java.math.BigDecimal

class PointWalletInput private constructor() {

    data class Charge(val loginId: String, val amount: BigDecimal) {
        fun toCommand(userId: Long): PointWalletCommand.Charge =
            PointWalletCommand.Charge(
                userId = userId,
                amount = amount,
            )
    }
}
