package com.loopers.interfaces.api.user

import com.loopers.domain.user.model.Gender.MALE
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

class UserV1ApiE2ETest : E2ETestSupport() {

    @Nested
    inner class `회원가입을 할 때, ` {

        private val requestUrl = "/api/v1/users"

        @Test
        fun `성별 정보가 누락되면, 400 Bad Request 응답을 반환한다`(
            @Autowired client: TestRestTemplate,
        ) {
            // Given
            val request = mapOf(
                "userId" to "wjsyuwls",
                "email" to "wjsyuwls@gmail.com",
                "birthdate" to "2000-01-01",
            )

            // When
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val actual = client.exchange(requestUrl, POST, HttpEntity(request), responseType)

            // Then
            assertAll(
                { assertThat(actual.statusCode.value()).isEqualTo(400) },
                { assertThat(actual.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(actual.body?.meta?.message).isEqualTo("필수 필드 'gender'이(가) 누락되었습니다.") },
            )
        }

        @Test
        fun `회원가입이 성공하면, 생성된 유저 정보를 응답으로 반환한다 `(
            @Autowired client: TestRestTemplate,
        ) {
            // Given
            val request = UserV1Dto.Request.SignUp(
                userId = "wjsyuwls",
                email = "wjsyuwls@gmail.com",
                birthdate = "2000-01-01",
                gender = MALE,
            )

            // When
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val actual = client.exchange(requestUrl, POST, HttpEntity(request), responseType)

            // Then
            assertAll(
                { assertThat(actual.statusCode.value()).isEqualTo(201) },
                { assertThat(actual.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
                { assertThat(actual.body?.data).usingRecursiveComparison().isEqualTo(request) },
            )
        }
    }

    @Nested
    inner class `사용자 정보를 조회할 때, ` {

        private val requestUrl = "/api/v1/users/me"

        @Test
        fun `존재하지 않는 ID로 조회하면, 404 Not Found 응답을 반환한다`(
            @Autowired client: TestRestTemplate,
        ) {
            // Given
            val nonExistentUserId = "wjsyuwls"
            val headers = HttpHeaders().apply { set("X-USER-ID", nonExistentUserId) }
            val httpEntity = HttpEntity(null, headers)

            // When
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val actual = client.exchange(requestUrl, GET, httpEntity, responseType)

            // Then
            assertAll(
                { assertThat(actual.statusCode.value()).isEqualTo(404) },
                { assertThat(actual?.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(actual?.body?.meta?.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `사용자 조회에 성공하면, 해당 유저 정보를 응답으로 반환한다`(
            @Autowired client: TestRestTemplate,
        ) {
            // Given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = MALE

            val signUpRequest = UserV1Dto.Request.SignUp(
                userId = userId,
                email = email,
                birthdate = birthdate,
                gender = gender,
            )

            val signUpResponseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            client.exchange("/api/v1/users", POST, HttpEntity(signUpRequest), signUpResponseType)

            val headers = HttpHeaders().apply { set("X-USER-ID", userId) }
            val httpEntity = HttpEntity(null, headers)

            // When
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val actual = client.exchange(requestUrl, GET, httpEntity, responseType)

            // Then
            assertAll(
                { assertThat(actual.statusCode.value()).isEqualTo(200) },
                { assertThat(actual.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
                { assertThat(actual.body?.data).usingRecursiveComparison().isEqualTo(signUpRequest) },
            )
        }
    }
}
