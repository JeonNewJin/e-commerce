package com.loopers.domain.like

import com.loopers.domain.like.entity.Like
import com.loopers.domain.like.entity.LikeCount
import com.loopers.domain.like.model.LikeableType.PRODUCT
import com.loopers.domain.like.vo.LikeTarget
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.test.context.event.ApplicationEvents

class LikeServiceTest(
    private val likeService: LikeService,
    @SpykBean
    private val likeRepository: LikeRepository,
    private val likeJpaRepository: LikeJpaRepository,
    private val likeCountJpaRepository: LikeCountJpaRepository,
    private val likeEventPublisher: LikeEventPublisher,
) : IntegrationTestSupport() {

    @Autowired
    lateinit var applicationEvents: ApplicationEvents

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
        }

        @Test
        fun `등록된 좋아요가 존재하지 않으면, 좋아요를 등록한다`() {
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
            val findLike = likeJpaRepository.findByUserIdAndTarget(
                userId,
                LikeTarget(targetId, targetType),
            )

            assertAll(
                { assertThat(findLike!!.userId).isEqualTo(1L) },
                { assertThat(findLike!!.target.id).isEqualTo(100L) },
                { assertThat(findLike!!.target.type).isEqualTo(PRODUCT) },
            )
        }

        @Test
        fun `좋아요가 정상적으로 등록되면, 좋아요 생성 이벤트가 발행된다`() {
            // given
            val userId = 1L
            val targetId = 100L
            val targetType = PRODUCT
            val command = LikeCommand.Like(
                userId = userId,
                targetId = targetId,
                targetType = targetType,
            )

            // when
            likeService.like(command)

            // then
            val eventCount = applicationEvents.stream(LikeEvent.LikeCreated::class.java)
                .filter { it.userId == userId && it.targetId == targetId && it.targetType == targetType }
                .count()

            assertThat(eventCount).isEqualTo(1)
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
        }

        @Test
        fun `등록된 좋아요가 존재하면, 좋아요를 삭제한다`() {
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
        }

        @Test
        fun `좋아요가 정상적으로 취소되면, 좋아요 취소 이벤트가 발행된다`() {
            // given
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

            // then
            val eventCount = applicationEvents.stream(LikeEvent.LikeDeleted::class.java)
                .filter { it.userId == userId && it.targetId == targetId && it.targetType == targetType }
                .count()

            assertThat(eventCount).isEqualTo(1)
        }
    }

    @Nested
    inner class `좋아요 목록을 조회할 때` {

        @Test
        fun `좋아요가 없으면 빈 목록을 반환한다`() {
            // Given
            val userId = 999L
            val targetType = PRODUCT

            val command = LikeCommand.FindLikes(
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
            val likes = (1..20).map { i ->
                Like(
                    userId = 1L,
                    targetId = i.toLong(),
                    targetType = PRODUCT,
                )
            }
            likeJpaRepository.saveAll(likes)

            val likeCounts = (1..20).map { i ->
                LikeCount(
                    targetType = PRODUCT,
                    targetId = i.toLong(),
                    count = i.toLong(),
                )
            }
            likeCountJpaRepository.saveAll(likeCounts)

            val command = LikeCommand.FindLikes(
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
                        .extracting("userId", "targetId", "targetType", "count")
                        .containsExactlyInAnyOrder(
                            tuple(1L, 20L, PRODUCT, 20L),
                            tuple(1L, 19L, PRODUCT, 19L),
                            tuple(1L, 18L, PRODUCT, 18L),
                            tuple(1L, 17L, PRODUCT, 17L),
                            tuple(1L, 16L, PRODUCT, 16L),
                            tuple(1L, 15L, PRODUCT, 15L),
                            tuple(1L, 14L, PRODUCT, 14L),
                            tuple(1L, 13L, PRODUCT, 13L),
                            tuple(1L, 12L, PRODUCT, 12L),
                            tuple(1L, 11L, PRODUCT, 11L),
                        )
                },
                { assertThat(actual.totalElements).isEqualTo(20) },
                { assertThat(actual.pageable.pageSize).isEqualTo(10) },
                { assertThat(actual.pageable.pageNumber).isEqualTo(0) },
            )
        }
    }
}
