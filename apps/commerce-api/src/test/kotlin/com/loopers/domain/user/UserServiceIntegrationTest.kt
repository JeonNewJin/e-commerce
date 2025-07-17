package com.loopers.domain.user

import com.loopers.domain.user.Gender.M
import com.loopers.utils.DatabaseCleanUp
import com.ninjasquad.springmockk.SpykBean
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class UserServiceIntegrationTest @Autowired constructor(
    @SpykBean
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @Nested
    inner class `사용자를 생성 할 때, ` {

        @Test
        fun `성공적으로 생성되면, 생성된 사용자 정보를 저장한다`() {
            // given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@google.com"
            val birthdate = "2000-01-01"
            val gender = M
            val command = UserCommand.Create(userId, email, birthdate, gender)

            // when
            userService.create(command)

            // then
            val result = userService.find(userId)

            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result!!.userId).isEqualTo(userId) },
                { assertThat(result!!.email).isEqualTo(email) },
                { assertThat(result!!.birthdate).isEqualTo(birthdate) },
                { assertThat(result!!.gender).isEqualTo(gender) },
            )
        }

        @Test
        fun `성공적으로 생성되면, 생성된 사용자 정보를 저장하고 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = M
            val command = UserCommand.Create(userId, email, birthdate, gender)

            // when
            val result = userService.create(command)

            // then
            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result.userId).isEqualTo(userId) },
                { assertThat(result.email).isEqualTo(email) },
                { assertThat(result.birthdate).isEqualTo(birthdate) },
                { assertThat(result.gender).isEqualTo(gender) },
            )

            verify(exactly = 1) {
                userRepository.save(
                    match {
                        it.userId == userId &&
                                it.email == email &&
                                it.birthdate == LocalDate.parse(birthdate) &&
                                it.gender == gender
                    },
                )
            }
        }
    }

    @Nested
    inner class `사용자 정보를 조회할 때, ` {

        @Test
        fun `존재하지 않는 사용자 ID로 조회하면, null을 반환한다`() {
            // given
            val userId = "wjsyuwls"

            // when
            val result = userService.find(userId)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `존재하는 사용자 ID로 조회하면, 해당 사용자 정보를 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = M

            userRepository.save(User(userId, email, birthdate, gender))

            // when
            val result = userService.find(userId)

            // then
            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result!!.userId).isEqualTo(userId) },
                { assertThat(result!!.email).isEqualTo(email) },
                { assertThat(result!!.birthdate).isEqualTo(birthdate) },
                { assertThat(result!!.gender).isEqualTo(gender) },
            )
        }
    }
}
