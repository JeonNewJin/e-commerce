package com.loopers.application.point

import com.loopers.domain.point.Point
import com.loopers.domain.point.PointRepository
import com.loopers.domain.user.Gender.M
import com.loopers.domain.user.User
import com.loopers.domain.user.UserRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
class PointFacadeIntegrationTest @Autowired constructor(
    private val pointFacade: PointFacade,
    private val pointRepository: PointRepository,
    private val userRepository: UserRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @Nested
    inner class `포인트를 조회할 때, ` {

        @Test
        fun `사용자가 존재하지 않으면, NOT_FOUND 예외가 발생한다`() {
            // given
            val userId = "wjsyuwls"

            // when
            val result = assertThrows<CoreException> {
                pointFacade.myPoints(userId)
            }

            // then
            assertAll(
                { assertThat(result.errorType).isEqualTo(NOT_FOUND) },
                { assertThat(result.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `포인트 정보가 없는 사용자를 조회하면, 0 포인트를 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = M

            userRepository.save(User(userId, email, birthdate, gender))

            // when
            val result = pointFacade.myPoints(userId)

            // then
            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result.userId).isEqualTo(userId) },
                { assertThat(result.balance).isEqualTo(BigDecimal.ZERO) },
            )
        }

        @Test
        fun `잔액이 정수일 경우, 소수점 이하를 생략하여 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = M

            userRepository.save(User(userId, email, birthdate, gender))

            val balance = BigDecimal(10_000L)
            pointRepository.save(Point(userId, balance))

            // when
            val result = pointFacade.myPoints(userId)

            // then
            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result.userId).isEqualTo(userId) },
                { assertThat(result.balance.toPlainString()).isEqualTo(balance.toPlainString()) },
            )
        }
    }
}
