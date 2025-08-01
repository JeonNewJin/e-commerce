package com.loopers.domain.user

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LoginIdTest {

    @Nested
    inner class `로그인 아이디 값 객체를 생성할 때, ` {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "", // 빈 문자열
                "wjsyuwls 1234", // 공백 포함
                "wjsyuwls1234!", // 특수문자 포함
                "전유진", // 한글 포함
                "wjsyuwls_1234", // 언더스코어 포함
                "wjsyuwls123", // 10자 초과
            ],
        )
        fun `로그인 아이디가 유효하지 않은 형식이면, BAD_REQUEST 예외가 발생한다`(invalidLoginId: kotlin.String) {
            // Given

            // When
            val actual = assertThrows<CoreException> {
                LoginId(invalidLoginId)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("아이디는 영문 및 숫자 10자 이내여야 합니다.") },
            )
        }

        @Test
        fun `유효한 로그인 아이디이면, 정상 생성된다`() {
            // Given
            val validLoginId = "wjsyuwls12"

            // When
            val actual = LoginId(validLoginId)

            // Then
            assertThat(actual.value).isEqualTo(validLoginId)
        }
    }
}
