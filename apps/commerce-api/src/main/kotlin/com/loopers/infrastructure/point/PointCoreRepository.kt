package com.loopers.infrastructure.point

import com.loopers.domain.point.Point
import com.loopers.domain.point.PointRepository
import org.springframework.stereotype.Component

@Component
class PointCoreRepository(private val pointJpaRepository: PointJpaRepository) : PointRepository {

    override fun find(userId: String): Point? = pointJpaRepository.findByUserId(userId)

    override fun save(point: Point) {
        pointJpaRepository.save(point)
    }
}
