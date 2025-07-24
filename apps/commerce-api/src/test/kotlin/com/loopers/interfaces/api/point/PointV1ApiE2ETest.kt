package com.loopers.interfaces.api.point

import com.loopers.domain.point.Point
import com.loopers.domain.point.PointRepository
import com.loopers.domain.user.Gender.M
import com.loopers.domain.user.User
import com.loopers.domain.user.UserRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.NOT_FOUND
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val pointRepository: PointRepository,
    private val userRepository: UserRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @Nested
    inner class `포인트 정보를 조회할 때, ` {

        var requestUrl = "/api/v1/points"

        @Test
        fun `X-USER-ID 헤더가 없으면, 400 Bad Request 응답을 반환한다`() {
            // given

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, GET, HttpEntity<Any>(Unit), responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(BAD_REQUEST) },
                { assertThat(response.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response.body!!.meta.errorCode).isEqualTo(BAD_REQUEST.reasonPhrase) },
                { assertThat(response.body!!.meta.message).isEqualTo("필수 요청 헤더 'X-USER-ID'가 누락되었습니다.") },
            )
        }

        @Test
        fun `포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = M
            userRepository.save(User(userId, email, birthdate, gender))

            val balance = BigDecimal(10_000L)
            pointRepository.save(Point(userId, balance))

            val headers = HttpHeaders().apply { set("X-USER-ID", userId) }
            val httpEntity = HttpEntity(null, headers)

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, GET, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(OK) },
                { assertThat(response.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
                { assertThat(response.body?.data?.balance).isEqualTo(balance) },
            )
        }
    }

    @Nested
    inner class `포인트를 충전할 때, ` {

        var requestUrl = "/api/v1/points/charge"

        @Test
        fun `사용자가 존재하지 않으면, 404 Not Found 응답을 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val amount = BigDecimal(10_000L)

            val headers = HttpHeaders().apply { set("X-USER-ID", userId) }
            val body = PointV1Dto.Request.Charge(amount)
            val httpEntity = HttpEntity(body, headers)

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(NOT_FOUND) },
                { assertThat(response.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response.body!!.meta.errorCode).isEqualTo(NOT_FOUND.reasonPhrase) },
                { assertThat(response.body!!.meta.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `1000 포인트 충전에 성공하면, 충전된 보유 총량을 응답으로 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = M
            userRepository.save(User(userId, email, birthdate, gender))

            val balance = BigDecimal(10_000L)
            pointRepository.save(Point(userId, balance))

            val amount = BigDecimal(1_000L)

            val headers = HttpHeaders().apply { set("X-USER-ID", userId) }
            val body = PointV1Dto.Request.Charge(amount)
            val httpEntity = HttpEntity(body, headers)

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(OK) },
                { assertThat(response.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
                { assertThat(response.body?.data?.balance).isEqualTo(balance + amount) },
            )
        }
    }
}
