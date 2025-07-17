package com.loopers.domain.point

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointService(private val pointRepository: PointRepository) {

    @Transactional(readOnly = true)
    fun find(userId: String): Point? = pointRepository.find(userId)

    @Transactional
    fun charge(command: PointCommand.Charge): Point {
        val point = (
                pointRepository.find(command.userId)
                    ?: Point(userId = command.userId)
                )

        point.charge(command.amount)

        pointRepository.save(point)

        return point
    }
}
