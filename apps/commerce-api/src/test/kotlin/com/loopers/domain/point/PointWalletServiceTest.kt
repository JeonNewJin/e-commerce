package com.loopers.domain.point

import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class PointWalletServiceTest(
    private val pointWalletRepository: PointWalletRepository,
    private val pointWalletService: PointWalletService,
) : IntegrationTestSupport() {

    @Nested
    inner class `포인트 정보를 조회할 때, ` {

        @Test
        fun `포인트 지갑이 존재하지 않는 사용자 ID로 조회하면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val nonExistentUserId = 1L

            // When
            val actual = assertThrows<CoreException> {
                pointWalletService.getPointWallet(nonExistentUserId)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 사용자의 포인트 지갑을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `포인트 지갑이 존재하는 사용자 ID로 조회하면, 정상 반환한다`() {
            // Given
            val userId = 1L
            val pointWallet = PointWallet(
                userId = userId,
                Point.of(10_000L),
            )
            pointWalletRepository.save(pointWallet)

            // When
            val actual = pointWalletService.getPointWallet(userId)

            // Then
            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual.userId).isEqualTo(1L) },
                { assertThat(actual.balance).isEqualTo(BigDecimal("10000.00")) },
            )
        }
    }

    @Nested
    inner class `포인트 지갑을 생성할 때, ` {

        @Test
        fun `이미 존재하는 사용자 ID로 생성하면, CONFLICT 예외가 발생한다`() {
            // Given
            val userId = 1L
            val pointWallet = PointWallet(userId = userId)
            pointWalletRepository.save(pointWallet)

            // When
            val actual = assertThrows<CoreException> {
                pointWalletService.create(userId)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(CONFLICT) },
                { assertThat(actual.message).isEqualTo("이미 포인트 지갑이 존재합니다.") },
            )
        }

        @Test
        fun `존재하지 않는 사용자 ID로 생성하면, 정상 생성된다`() {
            // Given
            val userId = 1L

            // When
            pointWalletService.create(userId)

            // Then
            val actual = pointWalletRepository.findByUserId(userId)

            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual!!.userId).isEqualTo(1L) },
                { assertThat(actual!!.balance.value).isEqualTo(BigDecimal("0.00")) },
            )
        }
    }

    @Nested
    inner class `포인트를 충전할 때, ` {

        @Test
        fun `포인트 지갑이 존재하지 않는 사용자 ID로 충전하면, NOT_FOUND 예외가 발생한다`() {
            // Given
            val command = PointWalletCommand.Charge(
                userId = 1L,
                amount = Point.of(10_000L),
            )

            // When
            val actual = assertThrows<CoreException> {
                pointWalletService.charge(command)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 사용자의 포인트 지갑을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `포인트 지갑이 존재하는 사용자 ID로 충전하면, 포인트를 충전하고 합산된 잔액을 저장한다`() {
            // Given
            val userId = 1L
            val balance = Point.of(5_000L)
            val chargeAmount = Point.of(1_000L)

            val pointWallet = PointWallet(
                userId = userId,
                balance = balance,
            )
            pointWalletRepository.save(pointWallet)

            val command = PointWalletCommand.Charge(userId, chargeAmount)

            // When
            pointWalletService.charge(command)

            // Then
            val actual = pointWalletRepository.findByUserId(userId)

            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual!!.userId).isEqualTo(1L) },
                { assertThat(actual!!.balance.value).isEqualTo(BigDecimal("6000.00")) },
            )
        }

        @Test
        fun `포인트 지갑이 존재하는 사용자 ID로 충전하면, 포인트를 충전하고 합산된 잔액을 반환한다`() {
            // Given
            val userId = 1L
            val balance = Point.of(5_000L)
            val chargeAmount = Point.of(1_000L)

            val pointWallet = PointWallet(
                userId = userId,
                balance = balance,
            )
            pointWalletRepository.save(pointWallet)

            val command = PointWalletCommand.Charge(userId, chargeAmount)

            // When
            val actual = pointWalletService.charge(command)

            // Then
            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual.userId).isEqualTo(1L) },
                { assertThat(actual.balance).isEqualTo(BigDecimal("6000.00")) },
            )
        }
    }
}
