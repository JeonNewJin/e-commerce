package com.loopers.domain.user

import com.loopers.domain.user.vo.Birthdate
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

class BirthdateTest {

    @Nested
    inner class `생년월일 값 객체를 생성할 때, ` {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "", // 빈 문자열
                "20000101", // 구분자 없음
                "2000/01/01", // 슬래시 구분자
                "20-01-01", // 연도 2자리
                "2000-1-1", // 월, 일 한 자리
                "2000-001-01", // 월 3자리
                "2000-01-011", // 일 3자리
                "2000-13-01", // 존재하지 않는 월
                "2000-12-32", // 존재하지 않는 일
                "2000-00-10", // 0월
                "abcd-ef-gh", // 문자 포함
                " 2000-01-01", // 앞에 공백
                "2000-01-01 ", // 뒤에 공백
                "2000.01.01", // 점 구분자
            ],
        )
        fun `생년월일이 유효하지 않은 형식이면, BAD_REQUEST 예외가 발생한다`(invalidEmail: String) {
            // Given

            // When
            val actual = assertThrows<CoreException> {
                Birthdate(invalidEmail)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("생년월일 형식이 올바르지 않습니다. (YYYY-MM-DD)") },
            )
        }

        @Test
        fun `유효한 생년월일이면, 정상 생성된다`() {
            // Given
            val validBirthdate = "2000-01-01"

            // When
            val actual = Birthdate(validBirthdate)

            // Then
            assertThat(actual.value).isEqualTo(validBirthdate)
        }
    }
}
