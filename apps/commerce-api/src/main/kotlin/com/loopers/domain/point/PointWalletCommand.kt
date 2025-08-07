package com.loopers.domain.point

import com.loopers.domain.point.vo.Point

class PointWalletCommand private constructor() {

    data class Charge(val userId: Long, val amount: Point)

    data class Use(val userId: Long, val amount: Point)
}
