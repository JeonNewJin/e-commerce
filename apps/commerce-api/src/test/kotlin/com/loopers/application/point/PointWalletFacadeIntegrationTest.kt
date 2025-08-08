package com.loopers.application.point

import com.loopers.domain.point.vo.Point
import com.loopers.domain.point.entity.PointWallet
import com.loopers.domain.point.PointWalletRepository
import com.loopers.domain.user.model.Gender.MALE
import com.loopers.domain.user.entity.User
import com.loopers.domain.user.UserRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

class PointWalletFacadeIntegrationTest @Autowired constructor(
    private val pointWalletFacade: PointWalletFacade,
    private val userRepository: UserRepository,
    private val pointWalletRepository: PointWalletRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `포인트를 조회할 때, ` {

        @Test
        fun `존재하지 않는 사용자 ID로 조회하면, NOT_FOUND 예외가 발생한다`() {
            // Given
            val loginId = "wjsyuwls"

            // When
            val actual = assertThrows<CoreException> {
                pointWalletFacade.getPoint(loginId)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `포인트 지갑이 존재하지 않는 사용자 ID로 조회하면, NOT_FOUND 예외가 발생한다`() {
            // Given
            val loginId = "wjsyuwls"
            val user = User(
                loginId = loginId,
                email = "wjsyuwls@google.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )
            userRepository.save(user)

            // When
            val actual = assertThrows<CoreException> {
                pointWalletFacade.getPoint(loginId)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 사용자의 포인트 지갑을 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `포인트 지갑이 존재하는 사용자 ID로 조회하면, 포인트 정보를 반환한다`() {
            // Given
            val loginId = "wjsyuwls"
            val user = User(
                loginId = loginId,
                email = "wjsyuwls@google.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )
            userRepository.save(user)

            val pointWallet = PointWallet(
                userId = user.id,
                balance = Point.of(5_000L),
            )
            pointWalletRepository.save(pointWallet)

            // When
            val actual = pointWalletFacade.getPoint(loginId)

            // Then
            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual.userId).isEqualTo(user.id) },
                { assertThat(actual.balance).isEqualTo(BigDecimal("5000.00")) },
            )
        }
    }

    @Nested
    inner class `포인트를 충전할 때, ` {

        @Test
        fun `존재하지 않는 사용자 ID로 충전하면, NOT_FOUND 예외가 발생한다`() {
            // Given
            val input = PointWalletInput.Charge(
                loginId = "wjsyuwls",
                amount = BigDecimal(1_000L),
            )

            // When
            val actual = assertThrows<CoreException> {
                pointWalletFacade.charge(input)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `존재하는 사용자 ID로 충전하면, 포인트가 충전되고 합산된 잔액을 반환한다`() {
            // Given
            val loginId = "wjsyuwls"
            val user = User(
                loginId = loginId,
                email = "wjsyuwls@google.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )
            userRepository.save(user)

            val pointWallet = PointWallet(
                userId = user.id,
                balance = Point.of(5_000L),
            )
            pointWalletRepository.save(pointWallet)

            val input = PointWalletInput.Charge(
                loginId = loginId,
                amount = BigDecimal(1_000L),
            )

            // When
            val actual = pointWalletFacade.charge(input)

            // Then
            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual.userId).isEqualTo(user.id) },
                { assertThat(actual.balance).isEqualTo(BigDecimal("6000.00")) },
            )
        }
    }
}
