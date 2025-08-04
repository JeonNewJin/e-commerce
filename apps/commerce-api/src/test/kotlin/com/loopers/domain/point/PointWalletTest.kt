package com.loopers.domain.point

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

class PointWalletTest {

    @Test
    fun `포인트 지갑이 정상 생성된다`() {
        // Given
        val userId = 1L
        val balance = Point.of(1000L)

        // When
        val actual = PointWallet(userId, balance)

        // Then
        assertAll(
            { assertThat(actual.userId).isEqualTo(userId) },
            { assertThat(actual.balance).isEqualTo(balance) },
        )
    }

    @Nested
    inner class `포인트를 충전할 때, ` {

        @Test
        fun `충전할 포인트가 0 이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val userId = 1L
            val balance = Point.of(1000L)
            val pointWallet = PointWallet(userId, balance)
            val chargeAmount = Point.ZERO

            // When
            val actual = assertThrows<CoreException> {
                pointWallet.charge(chargeAmount)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("충전할 포인트는 0보다 커야 합니다.") },
            )
        }

        @Test
        fun `충전할 포인트가 0 보다 크면, 정상 충전된다`() {
            // Given
            val userId = 1L
            val balance = Point.of(5000L)
            val pointWallet = PointWallet(userId, balance)
            val chargeAmount = Point.of(1L)
            val expectedBalance = balance.plus(chargeAmount)

            // When
            pointWallet.charge(chargeAmount)

            // Then
            assertThat(pointWallet.balance).isEqualTo(expectedBalance)
        }
    }

    @Nested
    inner class `포인트를 사용할 때, ` {

        @Test
        fun `사용할 포인트가 0 이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val userId = 1L
            val balance = Point.of(5000L)
            val pointWallet = PointWallet(userId, balance)
            val useAmount = Point.ZERO

            // When
            val actual = assertThrows<CoreException> {
                pointWallet.use(useAmount)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("사용할 포인트는 0보다 커야 합니다.") },
            )
        }

        @Test
        fun `사용할 포인트가 0 보다 크면, 정상 사용된다`() {
            // Given
            val userId = 1L
            val balance = Point.of(5000L)
            val pointWallet = PointWallet(userId, balance)
            val useAmount = Point.of(5000L)
            val expectedBalance = balance.minus(useAmount)

            // When
            pointWallet.use(useAmount)

            // Then
            assertThat(pointWallet.balance).isEqualTo(expectedBalance)
        }
    }
}
