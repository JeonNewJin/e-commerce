package com.loopers.domain.point

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointService(private val pointRepository: PointRepository) {

    @Transactional(readOnly = true)
    fun find(userId: String): Point? = pointRepository.find(userId)
}
