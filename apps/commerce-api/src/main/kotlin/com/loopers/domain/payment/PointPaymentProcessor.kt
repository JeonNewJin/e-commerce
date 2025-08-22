package com.loopers.domain.payment

import com.loopers.domain.payment.PaymentMethod.POINT
import com.loopers.domain.point.PointWalletRepository
import com.loopers.domain.point.vo.Point
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class PointPaymentProcessor(private val pointWalletRepository: PointWalletRepository) : PaymentProcessor {

    override fun paymentMethod(): PaymentMethod = POINT

    @Transactional
    override fun process(command: PaymentCommand.Pay) {
        val pointWallet = pointWalletRepository.findByUserIdWithLock(command.userId)
            ?: throw CoreException(NOT_FOUND, "해당 사용자의 포인트 지갑을 찾을 수 없습니다.")

        pointWallet.use(Point.of(command.amount))
        pointWalletRepository.save(pointWallet)
    }
}
