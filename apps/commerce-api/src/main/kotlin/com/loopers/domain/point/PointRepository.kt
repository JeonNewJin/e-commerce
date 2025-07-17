package com.loopers.domain.point

interface PointRepository {

    fun find(userId: String): Point?

    fun save(point: Point)
}
