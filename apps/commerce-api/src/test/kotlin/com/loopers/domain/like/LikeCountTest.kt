package com.loopers.domain.like

import com.loopers.domain.like.LikeableType.PRODUCT
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LikeCountTest {

    @Nested
    inner class `좋아요 카운트를 생성할 때, ` {

        @Test
        fun `좋아요 카운트가 음수이면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val targetId = 1L
            val targetType = PRODUCT
            val negativeCount = -1L

            // When
            val actual = assertThrows<CoreException> {
                LikeCount(
                    targetId = targetId,
                    targetType = targetType,
                    count = negativeCount,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("좋아요 카운트는 0 이상이어야 합니다.") },
            )
        }

        @Test
        fun `좋아요 카운트가 0 이상이면, 정상 생성된다`() {
            // Given
            val targetId = 1L
            val targetType = PRODUCT
            val count = 0L

            // When
            val actual = LikeCount(
                targetId = targetId,
                targetType = targetType,
                count = count,
            )

            // Then
            assertAll(
                { assertThat(actual.target.id).isEqualTo(1L) },
                { assertThat(actual.target.type).isEqualTo(PRODUCT) },
                { assertThat(actual.count).isEqualTo(0L) },
            )
        }
    }
}
