package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeCount
import com.loopers.domain.like.LikeRepository
import com.loopers.domain.like.LikeTarget
import com.loopers.domain.like.LikeTargetType
import org.springframework.stereotype.Component

@Component
class LikeCoreRepository(private val likeCountJpaRepository: LikeCountJpaRepository) : LikeRepository {

    override fun findLikeCountByTarget(likeTarget: LikeTarget): LikeCount? =
        likeCountJpaRepository.findByTarget(likeTarget)

    override fun findLikeCounts(
        targetType: LikeTargetType,
        targetIds: List<Long>,
    ): List<LikeCount> = likeCountJpaRepository.findByTargetTypeAndTargetIds(targetType, targetIds)
}
