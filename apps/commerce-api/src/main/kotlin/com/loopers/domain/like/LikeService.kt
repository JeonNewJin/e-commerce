package com.loopers.domain.like

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class LikeService(private val likeRepository: LikeRepository) {

    @Transactional
    fun like(command: LikeCommand.Like) {
        likeRepository.findByUserIdAndTarget(
            command.userId,
            LikeTarget(command.targetId, command.targetType),
        )?.let { return }

        val like = Like(
            userId = command.userId,
            targetId = command.targetId,
            targetType = command.targetType,
        )

        try {
            likeRepository.save(like)

            val likeCount = likeRepository.findLikeCountByTarget(like.target)
                ?: LikeCount(
                    targetId = command.targetId,
                    targetType = command.targetType,
                )

            likeCount.increase()
            likeRepository.saveLikeCount(likeCount)
        } catch (e: DataIntegrityViolationException) {
        }
    }

    @Transactional
    fun unlike(command: LikeCommand.Unlike) {
        val like = likeRepository.findByUserIdAndTarget(
            command.userId,
            LikeTarget(command.targetId, command.targetType),
        ) ?: return

        likeRepository.delete(like)

        val likeCount = likeRepository.findLikeCountByTarget(like.target)
            ?: return

        likeCount.decrease()
        likeRepository.saveLikeCount(likeCount)
    }

    fun getLikeCount(
        targetType: LikeableType,
        targetId: Long,
    ): LikeCountInfo =
        likeRepository.findLikeCountByTarget(LikeTarget(targetId, targetType))
            ?.let { LikeCountInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "좋아요 카운트를 찾을 수 없습니다.")

    fun findLikeCounts(
        targetType: LikeableType,
        targetIds: List<Long>,
    ): List<LikeCountInfo> =
        likeRepository.findLikeCounts(targetType, targetIds)
            .map { LikeCountInfo.from(it) }

    fun findLikes(command: LikeCommand.GetLikes): Page<LikeInfo> =
        likeRepository.findLikes(command)
            .map { LikeInfo.from(it) }
}
