package com.loopers.interfaces.api.point

import com.loopers.domain.point.vo.Point
import com.loopers.domain.point.entity.PointWallet
import com.loopers.domain.point.PointWalletRepository
import com.loopers.domain.user.model.Gender.MALE
import com.loopers.domain.user.entity.User
import com.loopers.domain.user.UserRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.E2ETestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import java.math.BigDecimal

class PointV1ApiE2ETest : E2ETestSupport() {

    @Nested
    inner class `포인트를 조회할 때, ` {

        var requestUrl = "/api/v1/points"

        @Test
        fun `X-USER-ID 헤더가 없으면, 400 Bad Request 응답을 반환한다`(
            @Autowired client: TestRestTemplate,
        ) {
            // Given

            // When
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val actual = client.exchange(requestUrl, GET, HttpEntity(Unit), responseType)

            // Then
            assertAll(
                { assertThat(actual.statusCode.value()).isEqualTo(400) },
                { assertThat(actual.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(actual.body!!.meta.message).isEqualTo("필수 요청 헤더 'X-USER-ID'가 누락되었습니다.") },
            )
        }

        @Test
        fun `포인트 조회에 성공하면, 보유 포인트를 응답으로 반환한다`(
            @Autowired client: TestRestTemplate,
            @Autowired userRepository: UserRepository,
            @Autowired pointWalletRepository: PointWalletRepository,
        ) {
            // Given
            val loginId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = MALE
            userRepository.save(User(loginId, email, birthdate, gender))

            val balance = Point.of(10_000L)
            val pointWallet = PointWallet(
                userId = 1L,
                balance = balance,
            )
            pointWalletRepository.save(pointWallet)

            val headers = HttpHeaders().apply { set("X-USER-ID", loginId) }
            val httpEntity = HttpEntity(null, headers)

            // When
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val actual = client.exchange(requestUrl, GET, httpEntity, responseType)

            // Then
            assertAll(
                { assertThat(actual.statusCode.value()).isEqualTo(200) },
                { assertThat(actual.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
                { assertThat(actual.body?.data?.balance?.compareTo(balance.value)).isEqualTo(0) },
            )
        }
    }

    @Nested
    inner class `포인트를 충전할 때, ` {

        var requestUrl = "/api/v1/points/charge"

        @Test
        fun `존재하지 않는 사용자 ID로 충전하면, 404 Not Found 응답을 반환한다`(
            @Autowired client: TestRestTemplate,
        ) {
            // Given
            val nonExistentUserId = "wjsyuwls"
            val chargeAmount = BigDecimal(10_000L)
            val request = PointV1Dto.Request.Charge(chargeAmount)

            val headers = HttpHeaders().apply { set("X-USER-ID", nonExistentUserId) }
            val httpEntity = HttpEntity(request, headers)

            // When
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val actual = client.exchange(requestUrl, POST, httpEntity, responseType)

            // Then
            assertAll(
                { assertThat(actual.statusCode.value()).isEqualTo(404) },
                { assertThat(actual.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(actual.body!!.meta.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `포인트 충전에 성공하면, 충전된 보유 총량을 응답으로 반환한다`(
            @Autowired client: TestRestTemplate,
            @Autowired userRepository: UserRepository,
            @Autowired pointWalletRepository: PointWalletRepository,
        ) {
            // Given
            val loginId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = MALE
            val user = User(
                loginId = loginId,
                email = email,
                birthdate = birthdate,
                gender = gender,
            )
            userRepository.save(user)

            val balance = Point.of(10_000L)
            val pointWallet = PointWallet(
                userId = user.id,
                balance = balance,
            )
            pointWalletRepository.save(pointWallet)

            val chargeAmount = BigDecimal(5_000L)
            val request = PointV1Dto.Request.Charge(chargeAmount)

            val headers = HttpHeaders().apply { set("X-USER-ID", loginId) }
            val httpEntity = HttpEntity(request, headers)

            // When
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val actual = client.exchange(requestUrl, POST, httpEntity, responseType)

            // Then
            assertAll(
                { assertThat(actual.statusCode.value()).isEqualTo(200) },
                { assertThat(actual.body!!.meta.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
                { assertThat(actual.body?.data?.balance?.compareTo(balance.value + chargeAmount)).isEqualTo(0) },
            )
        }
    }
}
