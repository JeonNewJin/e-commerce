package com.loopers.domain.point

import java.math.BigDecimal

object PointCommand {

    data class Charge(val userId: String, val amount: BigDecimal) {
        init {
            require(userId.isNotBlank()) { "User ID cannot be blank." }
        }
    }
}
