package com.loopers.domain.point

import java.math.BigDecimal

class PointWalletCommand private constructor() {

    data class Charge(val userId: Long, val amount: BigDecimal)

    data class Use(val userId: Long, val amount: BigDecimal)
}
