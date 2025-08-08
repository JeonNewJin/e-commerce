package com.loopers.domain.like

import com.loopers.domain.like.entity.Like
import com.loopers.domain.like.entity.LikeCount
import com.loopers.domain.like.model.LikeableType.PRODUCT
import com.loopers.domain.like.vo.LikeTarget
import com.loopers.infrastructure.like.LikeCountJpaRepository
import com.loopers.infrastructure.like.LikeJpaRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class LikeServiceIntegrationTest(
    private val likeService: LikeService,
    private val likeCountJpaRepository: LikeCountJpaRepository,
    private val likeJpaRepository: LikeJpaRepository,
) : IntegrationTestSupport() {

    @Test
    fun `좋아요 카운트가 0인 상품에, 3명의 사용자가 동시에 좋아요를 누르면, 모든 요청이 성공하고 좋아요 카운트가 3이 된다`() {
        // Given
        val targetId = 100L
        val targetType = PRODUCT
        val initialCount = 0L

        val likeCount = LikeCount(
            targetId = targetId,
            targetType = targetType,
            count = initialCount,
        )
        likeCountJpaRepository.save(likeCount)

        val threadCount = 3
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) { index ->
            val userId = (index + 1).toLong()
            executor.submit {
                try {
                    val command = LikeCommand.Like(
                        userId = userId,
                        targetId = targetId,
                        targetType = targetType,
                    )
                    likeService.like(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("좋아요 등록 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actualLikeCount = likeCountJpaRepository.findByTarget(LikeTarget(targetId, targetType))!!
        val actualLikes = likeJpaRepository.findAllByTarget(LikeTarget(targetId, targetType))

        assertThat(actualLikeCount.count).isEqualTo(3)
        assertThat(actualLikes).hasSize(3)

        assertThat(successCount.get()).isEqualTo(3)
        assertThat(failureCount.get()).isZero()
    }

    @Test
    fun `좋아요 카운트가 3인 상품에, 3명의 사용자가 동시에 좋아요를 취소하면, 모든 요청이 성공하고 좋아요 카운트가 0이 된다`() {
        // Given
        val targetId = 300L
        val targetType = PRODUCT
        val initialCount = 3L

        val likeCount = LikeCount(
            targetId = targetId,
            targetType = targetType,
            count = initialCount,
        )
        likeCountJpaRepository.save(likeCount)

        val initialUsers = (1..3).map { userId ->
            Like(
                userId = userId.toLong(),
                targetId = targetId,
                targetType = targetType,
            )
        }
        likeJpaRepository.saveAll(initialUsers)

        val threadCount = 3
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        repeat(threadCount) { index ->
            val userId = (index + 1).toLong()
            executor.submit {
                try {
                    val command = LikeCommand.Unlike(
                        userId = userId,
                        targetId = targetId,
                        targetType = targetType,
                    )
                    likeService.unlike(command)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    println("좋아요 취소 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actualLikeCount = likeCountJpaRepository.findByTarget(LikeTarget(targetId, targetType))!!
        val actualLikes = likeJpaRepository.findAllByTarget(LikeTarget(targetId, targetType))

        assertThat(actualLikeCount.count).isEqualTo(0)
        assertThat(actualLikes).isEmpty()

        assertThat(successCount.get()).isEqualTo(3)
        assertThat(failureCount.get()).isZero()
    }

    @Test
    fun `좋아요 카운트가 3인 상품에, 동시에 좋아요 등록 및 취소 요청을 각각 3번씩 보내면, 모든 요청이 성공하고 카운트는 3이 된다`() {
        // Given
        val targetId = 200L
        val targetType = PRODUCT
        val initialCount = 3L

        val likeCount = LikeCount(
            targetId = targetId,
            targetType = targetType,
            count = initialCount,
        )
        likeCountJpaRepository.save(likeCount)

        // 3명의 사용자가 이미 좋아요를 누른 상태
        val initialUsers = (1..3).map { userId ->
            Like(
                userId = userId.toLong(),
                targetId = targetId,
                targetType = targetType,
            )
        }
        likeJpaRepository.saveAll(initialUsers)

        val threadCount = 6 // 3명은 좋아요 취소, 3명은 좋아요 추가
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)
        val likeSuccessCount = AtomicInteger(0)
        val unlikeSuccessCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        // When
        // 기존 3명의 사용자가 좋아요 취소
        repeat(3) { index ->
            val userId = (index + 1).toLong() // 1~3 사용자 (기존 사용자들)
            executor.submit {
                try {
                    val command = LikeCommand.Unlike(
                        userId = userId,
                        targetId = targetId,
                        targetType = targetType,
                    )
                    likeService.unlike(command)
                    unlikeSuccessCount.incrementAndGet()
                } catch (e: Exception) {
                    println("좋아요 취소 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        // 새로운 3명의 사용자가 좋아요 추가
        repeat(3) { index ->
            val userId = (index + 4).toLong() // 4~6 사용자 (새로운 사용자들)
            executor.submit {
                try {
                    val command = LikeCommand.Like(
                        userId = userId,
                        targetId = targetId,
                        targetType = targetType,
                    )
                    likeService.like(command)
                    likeSuccessCount.incrementAndGet()
                } catch (e: Exception) {
                    println("좋아요 등록 실패: ${e.message}")
                    failureCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // Then
        val actualLikeCount = likeCountJpaRepository.findByTarget(LikeTarget(targetId, targetType))!!
        val actualLikes = likeJpaRepository.findAllByTarget(LikeTarget(targetId, targetType))

        assertThat(actualLikeCount.count).isEqualTo(3L)
        assertThat(actualLikes).hasSize(3)
        assertThat(actualLikes.map { it.userId }).containsExactlyInAnyOrder(4L, 5L, 6L)

        assertThat(likeSuccessCount.get()).isEqualTo(3)
        assertThat(unlikeSuccessCount.get()).isEqualTo(3)
        assertThat(failureCount.get()).isZero()
    }
}
