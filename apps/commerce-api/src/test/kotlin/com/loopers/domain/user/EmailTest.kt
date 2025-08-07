package com.loopers.domain.user

import com.loopers.domain.user.vo.Email
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.String

class EmailTest {

    @Nested
    inner class `이메일 값 객체를 생성할 때, ` {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "", // 빈 문자열
                "wjsyuwls@google.com ", // 후행 공백
                " wjsyuwls@google.com", // 선행 공백
                "wjsyuwlsgoogle.com", // @ 없음
                "@google.com", // 아이디 없음
                "wjsyuwls@", // 도메인 없음
                "wjsyuwls@.com", // 도메인 없음
                "wjsyuwls@google", // TLD 없음
                "wjsyuwls@google.c", // TLD 1자
                "wjsyuwls@google..com", // 연속된 점
                "wjsyuwls@google,com", // 잘못된 구분자
                "wjsyuwls@-google.com", // 도메인 시작 하이픈
                "wjsyuwls@google-.com", // 도메인 끝 하이픈
                "wjsyuwls@google_.com", // 도메인 언더스코어
            ],
        )
        fun `이메일이 유효하지 않은 형식이면, BAD_REQUEST 예외가 발생한다`(invalidValue: String) {
            // Given

            // When
            val actual = assertThrows<CoreException> {
                Email(invalidValue)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("이메일 형식이 올바르지 않습니다.") },
            )
        }

        @Test
        fun `유효한 이메일이면, 정상 생성된다`() {
            // Given
            val validEmail = "wjsyuwls@google.com"

            // When
            val actual = Email(validEmail)

            // Then
            assertThat(actual.value).isEqualTo(validEmail)
        }
    }
}
