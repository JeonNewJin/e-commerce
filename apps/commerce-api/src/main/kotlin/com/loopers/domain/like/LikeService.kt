package com.loopers.domain.like

import com.loopers.domain.like.entity.Like
import com.loopers.domain.like.entity.LikeCount
import com.loopers.domain.like.model.LikeCountInfo
import com.loopers.domain.like.model.LikeInfo
import com.loopers.domain.like.model.LikeableType
import com.loopers.domain.like.vo.LikeTarget
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
        likeRepository.save(like)

        val likeCount = likeRepository.findLikeCountByTargetWithLock(like.target)
            ?: LikeCount(
                targetId = command.targetId,
                targetType = command.targetType,
            )

        likeCount.increase()
        likeRepository.saveLikeCount(likeCount)
    }

    @Transactional
    fun unlike(command: LikeCommand.Unlike) {
        val like = likeRepository.findByUserIdAndTarget(
            command.userId,
            LikeTarget(command.targetId, command.targetType),
        ) ?: return

        likeRepository.delete(like)

        val likeCount = likeRepository.findLikeCountByTargetWithLock(like.target)
            ?: return

        likeCount.decrease()
        likeRepository.saveLikeCount(likeCount)
    }

    fun findLikes(command: LikeCommand.FindLikes): Page<LikeInfo> =
        likeRepository.findLikes(command)
            .map { LikeInfo.from(it) }

    fun getLikeCount(
        targetType: LikeableType,
        targetId: Long,
    ): LikeCountInfo =
        likeRepository.findLikeCountByTarget(LikeTarget(targetId, targetType))
            ?.let { LikeCountInfo.from(it) }
            ?: LikeCountInfo.empty(targetType, targetId)

    fun findLikeCounts(
        targetType: LikeableType,
        targetIds: List<Long>,
    ): List<LikeCountInfo> =
        likeRepository.findLikeCounts(targetType, targetIds)
            .map { LikeCountInfo.from(it) }
}
