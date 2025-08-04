package com.loopers.domain.like

import com.loopers.domain.like.LikeTargetType.PRODUCT
import com.loopers.infrastructure.like.LikeCountJpaRepository
import com.loopers.infrastructure.like.LikeJpaRepository
import com.loopers.support.IntegrationTestSupport
import com.ninjasquad.springmockk.SpykBean
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.data.domain.Pageable

class LikeServiceTest(
    private val likeService: LikeService,
    @SpykBean
    private val likeRepository: LikeRepository,
    private val likeJpaRepository: LikeJpaRepository,
    private val likeCountJpaRepository: LikeCountJpaRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `좋아요를 등록할 때 ` {

        @Test
        fun `이미 등록된 좋아요가 존재하면, 성공으로 간주하고 return한다`() {
            // Given
            val userId = 1L
            val targetId = 100L
            val targetType = PRODUCT

            val like = Like(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )
            likeJpaRepository.save(like)

            val command = LikeCommand.Like(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )

            // When
            likeService.like(command)

            // Then
            verify(exactly = 0) { likeRepository.save(any()) }
            verify(exactly = 0) { likeRepository.saveLikeCount(any()) }
        }

        @Test
        fun `등록된 좋아요가 존재하지 않으면, 좋아요를 등록하고, 좋아요 카운트를 증가시킨다`() {
            // Given
            val userId = 1L
            val targetId = 100L
            val targetType = PRODUCT
            val command = LikeCommand.Like(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )

            val likeCount = LikeCount(
                targetId = targetId,
                targetType = targetType,
                count = 10L,
            )
            likeCountJpaRepository.save(likeCount)

            // When
            likeService.like(command)

            // Then
            val findLike = likeJpaRepository.findByUserIdAndTarget(
                userId,
                LikeTarget(targetId, targetType),
            )

            assertAll(
                { assertThat(findLike!!.userId).isEqualTo(1L) },
                { assertThat(findLike!!.target.id).isEqualTo(100L) },
                { assertThat(findLike!!.target.type).isEqualTo(PRODUCT) },
            )

            val findLikeCount = likeCountJpaRepository.findByTarget(LikeTarget(targetId, targetType))

            assertAll(
                { assertThat(findLikeCount!!.target.id).isEqualTo(100L) },
                { assertThat(findLikeCount!!.target.type).isEqualTo(PRODUCT) },
                { assertThat(findLikeCount!!.count).isEqualTo(11L) },
            )
        }

        @Test
        fun `좋아요 카운트가 존재하지 않으면, 좋아요 카운트를 생성한다`() {
            // Given
            val userId = 1L
            val targetId = 100L
            val targetType = PRODUCT
            val command = LikeCommand.Like(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )

            // When
            likeService.like(command)

            // Then
            val actual = likeCountJpaRepository.findByTarget(LikeTarget(targetId, targetType))

            assertAll(
                { assertThat(actual).isNotNull() },
                { assertThat(actual!!.target.id).isEqualTo(100L) },
                { assertThat(actual!!.target.type).isEqualTo(PRODUCT) },
                { assertThat(actual!!.count).isEqualTo(1L) },
            )
        }
    }

    @Nested
    inner class `좋아요를 취소할 때` {

        @Test
        fun `등록된 좋아요가 존재하지 않으면, 성공으로 간주하고 return한다`() {
            // Given
            val userId = 1L
            val targetId = 100L
            val targetType = PRODUCT

            val command = LikeCommand.Unlike(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )

            // When
            likeService.unlike(command)

            // Then
            verify(exactly = 0) { likeRepository.delete(any()) }
            verify(exactly = 0) { likeRepository.saveLikeCount(any()) }
        }

        @Test
        fun `등록된 좋아요가 존재하면, 좋아요를 삭제하고 좋아요 카운트를 감소시킨다`() {
            // Given
            val userId = 1L
            val targetId = 100L
            val targetType = PRODUCT

            val like = Like(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )
            likeJpaRepository.save(like)

            val likeCount = LikeCount(
                targetId = targetId,
                targetType = targetType,
                count = 5L,
            )
            likeCountJpaRepository.save(likeCount)

            val command = LikeCommand.Unlike(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )

            // When
            likeService.unlike(command)

            // Then
            val findLike = likeJpaRepository.findByUserIdAndTarget(
                userId,
                LikeTarget(targetId, targetType),
            )
            assertThat(findLike).isNull()

            val findLikeCount = likeCountJpaRepository.findByTarget(LikeTarget(targetId, targetType))

            assertAll(
                { assertThat(findLikeCount!!.target.id).isEqualTo(100L) },
                { assertThat(findLikeCount!!.target.type).isEqualTo(PRODUCT) },
                { assertThat(findLikeCount!!.count).isEqualTo(4L) },
            )
        }

        @Test
        fun `좋아요 카운트가 존재하지 않으면, 좋아요만 삭제하고 return한다`() {
            // Given
            val userId = 1L
            val targetId = 100L
            val targetType = PRODUCT

            val like = Like(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )
            likeJpaRepository.save(like)

            val command = LikeCommand.Unlike(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )

            // When
            likeService.unlike(command)

            // Then
            val findLike = likeJpaRepository.findByUserIdAndTarget(
                userId,
                LikeTarget(targetId, targetType),
            )
            assertThat(findLike).isNull()

            val findLikeCount = likeCountJpaRepository.findByTarget(LikeTarget(targetId, targetType))
            assertThat(findLikeCount).isNull()
        }
    }

    @Nested
    inner class `좋아요 목록을 조회할 때` {

        @Test
        fun `좋아요가 없으면 빈 목록을 반환한다`() {
            // Given
            val userId = 999L
            val targetType = PRODUCT

            val command = LikeCommand.GetLikes(
                userId = userId,
                targetType = targetType,
                pageable = Pageable.ofSize(10),
            )

            // When
            val actual = likeService.findLikes(command)

            // Then
            assertThat(actual.content).isEmpty()
        }

        @Test
        fun `페이지네이션을 통해 좋아요 목록을 분할하여 조회할 수 있다`() {
            // Given
            (1..20).map { i ->
                val like = Like(
                    userId = 1L,
                    targetId = i.toLong(),
                    targetType = PRODUCT,
                )
                likeJpaRepository.save(like)
            }

            val command = LikeCommand.GetLikes(
                userId = 1L,
                targetType = PRODUCT,
                pageable = Pageable.ofSize(10),
            )

            // When
            val actual = likeService.findLikes(command)

            // Then
            assertAll(
                {
                    assertThat(actual.content).hasSize(10)
                        .extracting("userId", "targetId", "targetType")
                        .containsExactlyInAnyOrder(
                            tuple(1L, 20L, PRODUCT),
                            tuple(1L, 19L, PRODUCT),
                            tuple(1L, 18L, PRODUCT),
                            tuple(1L, 17L, PRODUCT),
                            tuple(1L, 16L, PRODUCT),
                            tuple(1L, 15L, PRODUCT),
                            tuple(1L, 14L, PRODUCT),
                            tuple(1L, 13L, PRODUCT),
                            tuple(1L, 12L, PRODUCT),
                            tuple(1L, 11L, PRODUCT),
                        )
                },
                { assertThat(actual.totalElements).isEqualTo(20) },
                { assertThat(actual.pageable.pageSize).isEqualTo(10) },
                { assertThat(actual.pageable.pageNumber).isEqualTo(0) },
            )
        }
    }
}
