package com.loopers.domain.point

import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
class PointServiceIntegrationTest @Autowired constructor(
    private val pointRepository: PointRepository,
    private val pointService: PointService,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @Nested
    inner class `포인트를 조회할 때, ` {

        @Test
        fun `포인트 정보가 존재하지 않는 사용자 ID로 조회하면, null을 반환한다`() {
            // given
            val userId = "wjsyuwls"

            // when
            val result = pointService.find(userId)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `포인트 정보가 존재하는 사용자 ID로 조회하면, 보유 포인트 정보를 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val balance = BigDecimal(10_000L)

            pointRepository.save(Point(userId, balance))

            // when
            val result = pointService.find(userId)

            // then
            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result!!.userId).isEqualTo(userId) },
                { assertThat(result!!.balance.compareTo(balance)).isEqualTo(0) },
            )
        }
    }
}
