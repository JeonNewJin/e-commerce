package com.loopers.domain.user

import com.loopers.domain.user.model.Gender.MALE
import com.loopers.domain.user.entity.User
import com.loopers.domain.user.vo.LoginId
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import com.ninjasquad.springmockk.SpykBean
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class UserServiceTest(
    @SpykBean
    private val userRepository: UserRepository,
    private val userService: UserService,
) : IntegrationTestSupport() {

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Nested
    inner class `사용자를 생성 할 때, ` {

        @Test
        fun `성공적으로 생성되면, 생성된 사용자 정보를 저장한다`() {
            // Given
            val command = UserCommand.Register(
                loginId = "wjsyuwls",
                email = "wjsyuwls@google.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )

            // When
            userService.register(command)

            // Then
            val actual = userService.getUser(LoginId(command.loginId))

            assertAll(
                { assertThat(actual).isNotNull() },
                {
                    assertThat(actual)
                        .extracting("loginId", "email", "birthdate", "gender")
                        .containsExactly("wjsyuwls", "wjsyuwls@google.com", "2000-01-01", MALE)
                },
            )
        }

        @Test
        fun `성공적으로 생성되면, 생성된 사용자 정보를 저장하고 반환한다`() {
            // Given
            val command = UserCommand.Register(
                loginId = "wjsyuwls",
                email = "wjsyuwls@google.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )

            // When
            userService.register(command)

            // Then
            val actual = userService.getUser(LoginId(command.loginId))

            assertAll(
                { assertThat(actual).isNotNull() },
                {
                    assertThat(actual)
                        .extracting("loginId", "email", "birthdate", "gender")
                        .containsExactly("wjsyuwls", "wjsyuwls@google.com", "2000-01-01", MALE)
                },
            )

            verify(exactly = 1) {
                userRepository.save(
                    match {
                        it.loginId.value == "wjsyuwls" &&
                                it.email.value == "wjsyuwls@google.com" &&
                                it.birthdate.value == "2000-01-01" &&
                                it.gender == MALE
                    },
                )
            }
        }
    }

    @Nested
    inner class `사용자 정보를 조회할 때, ` {

        @Test
        fun `존재하지 않는 사용자 ID로 조회하면, NOT_FOUND 예외가 발생한다`() {
            // Given
            val loginId = "notfound"

            // When
            val actual = assertThrows<CoreException> {
                userService.getUser(LoginId(loginId))
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(actual.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `존재하는 사용자 ID로 조회하면, 해당 사용자 정보를 반환한다`() {
            // Given
            val user = User(
                loginId = "wjsyuwls",
                email = "wjsyuwls@google.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )
            userJpaRepository.save(user)

            // When
            val actual = userService.getUser(user.loginId)

            // Then
            assertAll(
                { assertThat(actual).isNotNull() },
                {
                    assertThat(actual)
                        .extracting("loginId", "email", "birthdate", "gender")
                        .containsExactly("wjsyuwls", "wjsyuwls@google.com", "2000-01-01", MALE)
                },
            )
        }
    }
}
