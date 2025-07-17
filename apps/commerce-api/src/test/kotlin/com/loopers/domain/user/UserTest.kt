package com.loopers.domain.user

import com.loopers.domain.user.Gender.M
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

class UserTest {

    @Nested
    inner class `사용자 엔티티를 생성할 때, ` {

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
        fun `ID가 유효하지 않은 형식이면, BAD_REQUEST 예외가 발생한다`(invalidId: String) {
            // given
            val email = "wjsyuwls@google.com"
            val birthdate = "2000-01-01"
            val gender = M

            // when
            val result = assertThrows<CoreException> {
                User(
                    userId = invalidId,
                    email = email,
                    birthdate = birthdate,
                    gender = gender,
                )
            }

            // then
            assertAll(
                { assertThat(result.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(result.message).isEqualTo("아이디는 영문 및 숫자 10자 이내여야 합니다.") },
            )
        }

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
        fun `이메일이 유효하지 않은 형식이면, BAD_REQUEST 예외가 발생한다`(invalidEmail: String) {
            // given
            val userId = "wjsyuwls12"
            val birthdate = "2000-01-01"
            val gender = M

            // when
            val result = assertThrows<CoreException> {
                User(
                    userId = userId,
                    email = invalidEmail,
                    birthdate = birthdate,
                    gender = gender,
                )
            }

            // then
            assertAll(
                { assertThat(result.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(result.message).isEqualTo("이메일 형식이 올바르지 않습니다.") },
            )
        }

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
        fun `생년월일이 유효하지 않은 형식이면, BAD_REQUEST 예외가 발생한다`(invalidBirthdate: String) {
            // given
            val userId = "wjsyuwls12"
            val email = "wjsyuwls@google.com"
            val gender = M

            // when
            val result = assertThrows<CoreException> {
                User(
                    userId = userId,
                    email = email,
                    birthdate = invalidBirthdate,
                    gender = gender,
                )
            }

            // then
            assertAll(
                { assertThat(result.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(result.message).isEqualTo("생년월일 형식이 올바르지 않습니다.") },
            )
        }

        @Test
        fun `생년월일이 현재 날짜 이후라면, BAD_REQUEST 예외가 발생한다`() {
            // given
            val userId = "wjsyuwls12"
            val email = "wjsyuwls@google.com"
            val invalidBirthdate = LocalDate.now().plusDays(1).toString()
            val gender = M

            // when
            val result = assertThrows<CoreException> {
                User(
                    userId = userId,
                    email = email,
                    birthdate = invalidBirthdate,
                    gender = gender,
                )
            }

            // then
            assertAll(
                { assertThat(result.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(result.message).isEqualTo("생년월일은 오늘 이전 날짜여야 합니다.") },
            )
        }
    }
}
