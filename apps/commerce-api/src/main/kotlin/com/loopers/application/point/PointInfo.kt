package com.loopers.application.point

import com.loopers.domain.point.Point
import java.math.BigDecimal

data class PointInfo(val userId: String, val balance: BigDecimal) {
    companion object {
        fun from(point: Point): PointInfo =
            PointInfo(
                userId = point.userId,
                balance = point.balance.stripTrailingZeros(),
            )
    }
}
