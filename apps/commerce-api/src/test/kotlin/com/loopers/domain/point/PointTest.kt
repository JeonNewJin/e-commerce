package com.loopers.domain.point

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class PointTest {

    @Nested
    inner class `포인트 엔티티를 생성할 때, ` {

        @Test
        fun `잔액이 음수이면, ILLEGAL_ARGUMENT 예외가 발생한다`() {
            // given
            val userId = "wjsyuwls"
            val balance = BigDecimal(-1L)

            // when
            val result = assertThrows<IllegalArgumentException> {
                Point(userId, balance)
            }

            // then
            assertAll(
                { assertThat(result.javaClass).isEqualTo(IllegalArgumentException::class.java) },
                { assertThat(result.message).isEqualTo("잔액은 0보다 작을 수 없습니다.") },
            )
        }

        @Test
        fun `잔액이 0 이상이면, 정상적으로 생성된다`() {
            // given
            val userId = "wjsyuwls"
            val balance = BigDecimal.ONE

            // when
            val result = Point(userId, balance)

            // then
            assertAll(
                { assertThat(result.userId).isEqualTo(userId) },
                { assertThat(result.balance).isEqualTo(balance) },
            )
        }
    }

    @Nested
    inner class `포인트를 충전할 때,` {

        @Test
        fun `0 이하의 포인트를 충전하면, ILLEGAL_ARGUMENT 예외가 발생한다`() {
            // given
            val userId = "wjsyuwls"
            val balance = BigDecimal(10_000L)
            val point = Point(userId, balance)
            val amount = BigDecimal.ZERO

            // when
            val result = assertThrows<IllegalArgumentException> {
                point.charge(amount)
            }

            // then
            assertAll(
                { assertThat(result.javaClass).isEqualTo(IllegalArgumentException::class.java) },
                { assertThat(result.message).isEqualTo("0 이하의 정수로 포인트를 충전할 수 없습니다.") },
            )
        }

        @Test
        fun `0 보다 큰 포인트를 충전하면, 해당 포인트의 잔액이 정상적으로 증가한다`() {
            // given
            val userId = "wjsyuwls"
            val balance = BigDecimal(10_000L)
            val point = Point(userId, balance)
            val amount = BigDecimal.ONE

            // when
            point.charge(amount)

            // then
            assertThat(point.balance).isEqualTo(balance + amount)
        }
    }
}
