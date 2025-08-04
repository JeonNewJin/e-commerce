package com.loopers.domain.point

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
data class Point private constructor(
    @Column(name = "point")
    val value: BigDecimal,
) : Comparable<Point> {

    companion object {
        val ZERO: Point = Point(BigDecimal.ZERO)

        fun of(value: Number): Point {
            val decimal = when (value) {
                is BigDecimal -> value
                is Long, is Int -> BigDecimal.valueOf(value.toLong())
                is Double, is Float -> BigDecimal.valueOf(value.toDouble())
                else -> throw CoreException(BAD_REQUEST, "지원하지 않는 숫자 타입입니다.")
            }

            require(decimal >= BigDecimal.ZERO) {
                throw CoreException(BAD_REQUEST, "포인트는 0 이상이어야 합니다.")
            }

            return Point(decimal)
        }
    }

    fun isNotZero(): Boolean = this.value != BigDecimal.ZERO

    fun plus(other: Point): Point = Point(this.value + other.value)
    fun minus(other: Point): Point = Point(this.value - other.value)

    override operator fun compareTo(other: Point): Int = this.value.compareTo(other.value)
}
