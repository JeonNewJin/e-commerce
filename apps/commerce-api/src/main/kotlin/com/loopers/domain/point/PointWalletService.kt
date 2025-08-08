package com.loopers.domain.point

import com.loopers.domain.point.entity.PointWallet
import com.loopers.domain.point.model.PointWalletInfo
import com.loopers.domain.point.vo.Point
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PointWalletService(private val pointWalletRepository: PointWalletRepository) {

    fun getPointWallet(userId: Long): PointWalletInfo =
        pointWalletRepository.findByUserId(userId)
            ?.let { PointWalletInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "해당 사용자의 포인트 지갑을 찾을 수 없습니다.")

    @Transactional
    fun create(userId: Long) {
        pointWalletRepository.findByUserId(userId)
            ?.let { throw CoreException(CONFLICT, "이미 포인트 지갑이 존재합니다.") }

        val pointWallet = PointWallet(userId)
        pointWalletRepository.save(pointWallet)
    }

    @Transactional
    fun charge(command: PointWalletCommand.Charge): PointWalletInfo {
        val pointWallet = pointWalletRepository.findByUserIdWithLock(command.userId)
            ?: throw CoreException(NOT_FOUND, "해당 사용자의 포인트 지갑을 찾을 수 없습니다.")

        pointWallet.charge(Point.of(command.amount))
        pointWalletRepository.save(pointWallet)
        return PointWalletInfo.from(pointWallet)
    }

    @Transactional
    fun use(command: PointWalletCommand.Use): PointWalletInfo {
        val pointWallet = pointWalletRepository.findByUserIdWithLock(command.userId)
            ?: throw CoreException(NOT_FOUND, "해당 사용자의 포인트 지갑을 찾을 수 없습니다.")

        pointWallet.use(Point.of(command.amount))
        pointWalletRepository.save(pointWallet)
        return PointWalletInfo.from(pointWallet)
    }
}
