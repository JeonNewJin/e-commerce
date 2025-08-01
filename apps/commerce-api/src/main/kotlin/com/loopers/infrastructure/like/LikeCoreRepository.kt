package com.loopers.infrastructure.like

import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeCommand
import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.LikeRepository
import com.loopers.domain.like.LikeTarget
import com.loopers.domain.like.LikeTargetType
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class LikeCoreRepository(
    private val likeCountJpaRepository: LikeCountJpaRepository,
    private val likeJpaRepository: LikeJpaRepository,
    private val customRepository: LikeCustomRepository,
) : LikeRepository {

    override fun findByUserIdAndTarget(
        userId: Long,
        likeTarget: LikeTarget,
    ): Like? = likeJpaRepository.findByUserIdAndTarget(userId, likeTarget)

    override fun save(like: Like) {
        likeJpaRepository.save(like)
    }

    override fun saveLikeCount(likeCount: LikeCount) {
        likeCountJpaRepository.save(likeCount)
    }

    override fun delete(like: Like) {
        likeJpaRepository.delete(like)
    }

    override fun findLikeCountByTarget(likeTarget: LikeTarget): LikeCount? =
        likeCountJpaRepository.findByTarget(likeTarget)

    override fun findLikeCounts(
        targetType: LikeTargetType,
        targetIds: List<Long>,
    ): List<LikeCount> = likeCountJpaRepository.findByTargetTypeAndTargetIds(targetType, targetIds)

    override fun findLikes(command: LikeCommand.GetLikes): Page<Like> = customRepository.findLikes(command)
}
