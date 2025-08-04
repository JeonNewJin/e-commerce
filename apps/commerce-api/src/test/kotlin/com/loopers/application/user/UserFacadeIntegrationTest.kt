package com.loopers.application.user

import com.loopers.domain.point.PointWalletRepository
import com.loopers.domain.user.Gender.MALE
import com.loopers.domain.user.User
import com.loopers.domain.user.UserRepository
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

class UserFacadeIntegrationTest(
    private val userRepository: UserRepository,
    private val userFacade: UserFacade,
    private val pointWalletRepository: PointWalletRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `회원가입을 할 때, ` {

        @Test
        fun `이미 가입된 ID로 회원가입을 시도하면, CONFLICT 예외가 발생한다`() {
            // Given
            userRepository.save(
                User(
                    loginId = "wjsyuwls",
                    email = "wjsyuwls@gmail.com",
                    birthdate = "2000-01-01",
                    gender = MALE,
                ),
            )

            val input = UserInput.Register(
                loginId = "wjsyuwls",
                email = "wjsyuwls@gmail.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )

            // When
            val actual = assertThrows<CoreException> {
                userFacade.signUp(input)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(CONFLICT) },
                { assertThat(actual.message).isEqualTo("동일한 ID로 이미 가입된 계정이 존재합니다.") },
            )
        }

        @Test
        fun `회원가입에 성공하면, 포인트 지갑이 생성된다`() {
            // Given
            val input = UserInput.Register(
                loginId = "wjsyuwls",
                email = "wjsyuwls@gmail.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )

            // When
            userFacade.signUp(input)

            // Then
            val actual = pointWalletRepository.findByUserId(1L)

            assertAll(
                { assertThat(actual).isNotNull },
                { assertThat(actual?.userId).isEqualTo(1L) },
                { assertThat(actual?.balance?.value).isEqualTo(BigDecimal("0.00")) },
            )
        }
    }

    @Test
    fun `사용자 정보를 조회할 때, 존재하지 않는 사용자 ID로 조회하면, NOT_FOUND 예외가 발생한다`() {
        // Given
        val userId = "notfound"

        // When
        val actual = assertThrows<CoreException> {
            userFacade.getUser(userId)
        }

        // Then
        assertAll(
            { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
            { assertThat(actual.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
        )
    }
}
