package com.loopers.domain.like

import com.loopers.domain.like.model.LikeableType.PRODUCT
import com.loopers.domain.like.entity.Like
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

class LikeTest {

    @Nested
    inner class `좋아요 정보를 생성할 때, ` {

        @Test
        fun `사용자 ID가 유효하지 않으면, BAD_REQUEST 예외가 발생한다`() {
            // Given
            val invalidUserId = 0L
            val targetId = 1L
            val targetType = PRODUCT

            // When
            val actual = assertThrows<CoreException> {
                Like(
                    userId = invalidUserId,
                    targetId = targetId,
                    targetType = targetType,
                )
            }

            // Then
            assertAll(
                { assertThat(actual.errorType).isEqualTo(BAD_REQUEST) },
                { assertThat(actual.message).isEqualTo("유효하지 않은 사용자 ID 입니다.") },
            )
        }

        @Test
        fun `유효한 정보로 정상 생성된다`() {
            // Given
            val userId = 1L
            val targetId = 1L
            val targetType = PRODUCT

            // When
            val actual = Like(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )

            // Then
            assertAll(
                { assertThat(actual.userId).isEqualTo(1L) },
                { assertThat(actual.target.id).isEqualTo(1L) },
                { assertThat(actual.target.type).isEqualTo(PRODUCT) },
            )
        }
    }
}
