package com.loopers.application.point

import com.loopers.domain.point.PointCommand
import com.loopers.domain.point.PointService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
class PointFacade(private val userService: UserService, private val pointService: PointService) {

    @Transactional(readOnly = true)
    fun myPoints(userId: String): PointInfo {
        userService.find(userId)
            ?: throw CoreException(NOT_FOUND, "해당 사용자를 찾을 수 없습니다.")

        return pointService.find(userId)
            ?.let { PointInfo.from(it) }
            ?: PointInfo(userId = userId, balance = BigDecimal.ZERO)
    }

    @Transactional
    fun charge(command: PointCommand.Charge): PointInfo {
        userService.find(command.userId)
            ?: throw CoreException(NOT_FOUND, "해당 사용자를 찾을 수 없습니다.")

        return pointService.charge(command)
            .let { PointInfo.from(it) }
    }
}
