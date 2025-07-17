package com.loopers.application.user

import com.loopers.domain.user.Gender.M
import com.loopers.domain.user.User
import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserFacadeIntegrationTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userFacade: UserFacade,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @Test
    fun `회원가입을 할 때,  이미 가입된 ID로 회원가입을 시도하면, CONFLICT 예외가 발생한다`() {
        // given
        val userId = "wjsyuwls"
        val email = "wjsyuwls@gmail.com"
        val birthdate = "2000-01-01"
        val gender = M

        userRepository.save(
            User(
                userId = userId,
                email = email,
                birthdate = birthdate,
                gender = gender,
            ),
        )

        val command = UserCommand.Create(userId, email, birthdate, gender)

        // when
        val result = assertThrows<CoreException> {
            userFacade.signup(command)
        }

        // then
        assertAll(
            { assertThat(result.errorType).isEqualTo(CONFLICT) },
            { assertThat(result.message).isEqualTo("동일한 ID로 이미 가입된 계정이 존재합니다.") },
        )
    }
}
