package com.loopers.domain.point

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "point")
class Point(userId: String, balance: BigDecimal = BigDecimal.ZERO) : BaseEntity() {

    @Column(name = "user_id", nullable = false)
    val userId: String = userId

    @Column(name = "balance", nullable = false)
    var balance: BigDecimal = balance
        private set

    init {
        require(balance >= BigDecimal.ZERO) { "잔액은 0보다 작을 수 없습니다." }
    }
}
