package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.LikeTarget
import com.loopers.domain.like.LikeableType.PRODUCT
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test

class LikeCoreRepositoryTest(
    private val likeCoreRepository: LikeCoreRepository,
    private val likeCountJpaRepository: LikeCountJpaRepository,
) : IntegrationTestSupport() {

    @Test
    fun `좋아요 대상 타입과 대상 아이디 목록으로 조회하면 좋아요 카운트 목록을 반환한다`() {
        // Given
        val likeCounts = (1..5).map { i ->
            LikeCount(
                targetId = i.toLong(),
                targetType = PRODUCT,
                count = i.toLong(),
            )
        }

        likeCountJpaRepository.saveAll(likeCounts)

        val targetType = PRODUCT
        val targetIds = listOf(1L, 2L, 3L, 4L, 5L)

        // When
        val actual = likeCoreRepository.findLikeCounts(
            targetType = targetType,
            targetIds = targetIds,
        )

        // Then
        assertThat(actual).hasSize(5)
            .extracting("target", "count")
            .containsExactlyInAnyOrder(
                tuple(LikeTarget(1L, PRODUCT), 1L),
                tuple(LikeTarget(2L, PRODUCT), 2L),
                tuple(LikeTarget(3L, PRODUCT), 3L),
                tuple(LikeTarget(4L, PRODUCT), 4L),
                tuple(LikeTarget(5L, PRODUCT), 5L),
            )
    }
}
