package com.loopers.domain.point

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class PointTest {

    @Nested
    inner class `포인트 값 객체를 생성할 때, ` {

        @Test
        fun `포인트가 음수이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val negativePoint = BigDecimal(-1L)

            // When
            val actual = assertThrows<CoreException> {
                Point(negativePoint)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("포인트는 0 이상이어야 합니다.") },
            )
        }

        @Test
        fun `포인트가 0 이상이면, 정상 생성된다`() {
            // Given
            val point = BigDecimal.ZERO

            // When
            val actual = Point(point)

            // Then
            assertThat(actual.value).isEqualTo(point)
        }
    }
}
