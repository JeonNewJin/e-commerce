package com.loopers.interfaces.api.user

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
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val userRepository: UserRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @Nested
    inner class `회원가입을 할 때, ` {

        val requestUrl = "/api/v1/users"

        @Test
        fun `회원가입이 성공하면, 생성된 유저 정보를 응답으로 반환한다 `() {
            // given
            val request = UserV1Dto.Request.Signup(
                userId = "wjsyuwls",
                email = "wjsyuwls@gmail.com",
                birthdate = "2000-01-01",
                gender = M,
            )

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, POST, HttpEntity<Any>(request), responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(CREATED) },
                { assertThat(response.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
                { assertThat(response.body?.data?.userId).isEqualTo(request.userId) },
                { assertThat(response.body?.data?.email).isEqualTo(request.email) },
                { assertThat(response.body?.data?.birthdate).isEqualTo(request.birthdate) },
                { assertThat(response.body?.data?.gender).isEqualTo(request.gender) },
            )
        }

        @Test
        fun `성별 정보가 누락되면, 400 Bad Request 응답을 반환한다`() {
            // given
            val request = mapOf(
                "userId" to "wjsyuwls",
                "email" to "wjsyuwls@gmail.com",
                "birthdate" to "2000-01-01",
            )

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, POST, HttpEntity<Any>(request), responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(BAD_REQUEST) },
                { assertThat(response?.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response?.body?.meta?.errorCode).isEqualTo(BAD_REQUEST.reasonPhrase) },
                { assertThat(response?.body?.meta?.message).isEqualTo("필수 필드 'gender'이(가) 누락되었습니다.") },
            )
        }

        @Test
        fun `사용자 아이디 정보가 누락되면, 400 Bad Request 응답을 반환한다`() {
            // given
            val request = mapOf(
                "email" to "wjsyuwls@gmail.com",
                "birthdate" to "2000-01-01",
                "gender" to "M",
            )

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, POST, HttpEntity<Any>(request), responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(BAD_REQUEST) },
                { assertThat(response?.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response?.body?.meta?.errorCode).isEqualTo(BAD_REQUEST.reasonPhrase) },
                { assertThat(response?.body?.meta?.message).isEqualTo("필수 필드 'userId'이(가) 누락되었습니다.") },
            )
        }

        @Test
        fun `이메일 정보가 누락되면, 400 Bad Request 응답을 반환한다`() {
            // given
            val request = mapOf(
                "userId" to "wjsyuwls",
                "birthdate" to "2000-01-01",
                "gender" to "M",
            )

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, POST, HttpEntity<Any>(request), responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(BAD_REQUEST) },
                { assertThat(response?.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response?.body?.meta?.errorCode).isEqualTo(BAD_REQUEST.reasonPhrase) },
                { assertThat(response?.body?.meta?.message).isEqualTo("필수 필드 'email'이(가) 누락되었습니다.") },
            )
        }

        @Test
        fun `생년월일 정보가 누락되면, 400 Bad Request 응답을 반환한다`() {
            // given
            val request = mapOf(
                "userId" to "wjsyuwls",
                "email" to "wjsyuwls@gmail.com",
                "gender" to "M",
            )

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, POST, HttpEntity<Any>(request), responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(BAD_REQUEST) },
                { assertThat(response?.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response?.body?.meta?.errorCode).isEqualTo(BAD_REQUEST.reasonPhrase) },
                { assertThat(response?.body?.meta?.message).isEqualTo("필수 필드 'birthdate'이(가) 누락되었습니다.") },
            )
        }
    }

    @Nested
    inner class `사용자 정보를 조회할 때, ` {

        val requestUrl = "/api/v1/users/me"

        @Test
        fun `존재하지 않는 ID로 조회하면, 404 Not Found 응답을 반환한다`() {
            // given
            val userId = "wjsyuwls"

            val headers = HttpHeaders().apply { set("X-USER-ID", userId) }
            val httpEntity = HttpEntity(null, headers)

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, GET, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(NOT_FOUND) },
                { assertThat(response?.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response?.body?.meta?.errorCode).isEqualTo(NOT_FOUND.reasonPhrase) },
                { assertThat(response?.body?.meta?.message).isEqualTo("해당 사용자를 찾을 수 없습니다.") },
            )
        }

        @Test
        fun `사용자 조회에 성공하면, 해당 유저 정보를 응답으로 반환한다`() {
            // given
            val userId = "wjsyuwls"
            val email = "wjsyuwls@gmail.com"
            val birthdate = "2000-01-01"
            val gender = M
            userRepository.save(User(userId, email, birthdate, gender))

            val headers = HttpHeaders().apply { set("X-USER-ID", userId) }
            val httpEntity = HttpEntity(null, headers)

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, GET, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(response.statusCode).isEqualTo(OK) },
                { assertThat(response.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.SUCCESS) },
                { assertThat(response.body?.data?.userId).isEqualTo(userId) },
                { assertThat(response.body?.data?.email).isEqualTo(email) },
                { assertThat(response.body?.data?.birthdate).isEqualTo(birthdate) },
                { assertThat(response.body?.data?.gender).isEqualTo(gender) },
            )
        }
    }
}
