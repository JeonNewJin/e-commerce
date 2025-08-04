package com.loopers.domain.like

import com.loopers.domain.like.LikeableType.BRAND
import com.loopers.domain.like.LikeableType.PRODUCT
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

class LikeTargetTest {

    @Nested
    inner class `좋아요 대상 값 객체를 생성할 때, ` {

        @Test
        fun `브랜드 ID가 유효하지 않으면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val invalidId = 0L

            // When
            val actual = assertThrows<CoreException> {
                LikeTarget(invalidId, BRAND)
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("유효하지 않은 대상 ID 입니다.") },
            )
        }

        @Test
        fun `유효한 정보로 정상 생성된다`() {
            // Given
            val targetId = 1L
            val targetType = PRODUCT

            // When
            val actual = LikeTarget(targetId, targetType)

            // Then
            assertAll(
                { assertThat(actual.id).isEqualTo(1L) },
                { assertThat(actual.type).isEqualTo(PRODUCT) },
            )
        }
    }
}
