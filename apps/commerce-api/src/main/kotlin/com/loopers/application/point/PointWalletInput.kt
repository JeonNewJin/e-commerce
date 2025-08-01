package com.loopers.application.point

import com.loopers.domain.point.Point
import com.loopers.domain.point.PointWalletCommand

class PointWalletInput private constructor() {

    data class Charge(val loginId: String, val amount: Point) {
        fun toCommand(userId: Long): PointWalletCommand.Charge =
            PointWalletCommand.Charge(
                userId = userId,
                amount = amount,
            )
    }
}
