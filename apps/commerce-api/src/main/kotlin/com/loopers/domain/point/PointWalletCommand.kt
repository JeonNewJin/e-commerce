package com.loopers.domain.point

class PointWalletCommand private constructor() {

    data class Charge(val userId: Long, val amount: Point)

    data class Use(val userId: Long, val amount: Point)
}
