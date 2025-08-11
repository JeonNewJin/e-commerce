package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeCommand
import com.loopers.domain.like.LikeRepository
import com.loopers.domain.like.entity.Like
import com.loopers.domain.like.entity.LikeCount
import com.loopers.domain.like.model.LikeWithCount
import com.loopers.domain.like.model.LikeableType
import com.loopers.domain.like.vo.LikeTarget
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

    override fun delete(like: Like) {
        likeJpaRepository.delete(like)
    }

    override fun saveLikeCount(likeCount: LikeCount) {
        likeCountJpaRepository.save(likeCount)
    }

    override fun findLikeCountByTarget(likeTarget: LikeTarget): LikeCount? =
        likeCountJpaRepository.findByTarget(likeTarget)

    override fun findLikeCounts(
        targetType: LikeableType,
        targetIds: List<Long>,
    ): List<LikeCount> = likeCountJpaRepository.findByTargetTypeAndTargetIds(targetType, targetIds)

    override fun findLikes(command: LikeCommand.FindLikes): Page<LikeWithCount> = customRepository.findLikes(command)

    override fun findLikeCountByTargetWithLock(likeTarget: LikeTarget): LikeCount? =
        likeCountJpaRepository.findByTargetWithLock(likeTarget)
}
