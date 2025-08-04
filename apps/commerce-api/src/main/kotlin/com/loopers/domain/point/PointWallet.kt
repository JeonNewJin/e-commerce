package com.loopers.domain.point

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "point_wallet")
class PointWallet(userId: Long, balance: Point = Point.ZERO) : BaseEntity() {

    val userId: Long = userId

    @Embedded
    var balance: Point = balance
        private set

    fun charge(amount: Point) {
        require(amount.isNotZero()) {
            throw CoreException(BAD_REQUEST, "충전할 포인트는 0보다 커야 합니다.")
        }

        balance = balance.plus(amount)
    }

    fun use(amount: Point) {
        require(amount.isNotZero()) {
            throw CoreException(BAD_REQUEST, "사용할 포인트는 0보다 커야 합니다.")
        }
        require(balance >= amount) {
            throw CoreException(BAD_REQUEST, "잔액이 부족합니다.")
        }

        balance = balance.minus(amount)
    }
}
