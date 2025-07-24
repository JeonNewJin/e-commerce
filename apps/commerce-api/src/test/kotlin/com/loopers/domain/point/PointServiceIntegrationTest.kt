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

    @Nested
    inner class `포인트를 충전할 때, ` {

        @Test
        fun `포인트 정보가 존재하지 않는 사용자 ID로 충전하면, 포인트 정보를 생성하고 잔액을 충전 금액으로 초기화하여 저장한다`() {
            // given
            val userId = "wjsyuwls"
            val amount = BigDecimal(10_000L)
            val command = PointCommand.Charge(userId, amount)

            // when
            pointService.charge(command)

            // then
            val result = pointRepository.find(userId)

            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result!!.userId).isEqualTo(userId) },
                { assertThat(result!!.balance.compareTo(amount)).isEqualTo(0) },
            )
        }

        @Test
        fun `포인트 정보가 존재하지 않는 사용자 ID로 충전하면, 포인트 정보를 생성하고 잔액을 충전 금액으로 초기화하여 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val amount = BigDecimal(10_000L)
            val command = PointCommand.Charge(userId, amount)

            // when
            val result = pointService.charge(command)

            // then
            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result.userId).isEqualTo(userId) },
                { assertThat(result.balance).isEqualTo(amount) },
            )
        }

        @Test
        fun `포인트 정보가 존재하는 사용자 ID로 충전하면, 포인트를 충전하고 합산된 잔액을 저장한다`() {
            // given
            val userId = "wjsyuwls"
            val balance = BigDecimal(5_000L)

            pointRepository.save(Point(userId, balance))

            val amount = BigDecimal(10_000L)
            val command = PointCommand.Charge(userId, amount)

            // when
            pointService.charge(command)

            // then
            val result = pointRepository.find(userId)

            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result!!.userId).isEqualTo(userId) },
                { assertThat(result!!.balance.compareTo(balance + amount)).isEqualTo(0) },
            )
        }

        @Test
        fun `포인트 정보가 존재하는 사용자 ID로 충전하면, 포인트를 충전하고 합산된 잔액을 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val balance = BigDecimal(5_000L)

            pointRepository.save(Point(userId, balance))

            val amount = BigDecimal(10_000L)
            val command = PointCommand.Charge(userId, amount)

            // when
            val result = pointService.charge(command)

            // then
            assertAll(
                { assertThat(result).isNotNull() },
                { assertThat(result.userId).isEqualTo(userId) },
                { assertThat(result.balance.compareTo(balance + amount)).isEqualTo(0) },
            )
        }
    }
}
