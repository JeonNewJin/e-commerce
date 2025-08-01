package com.loopers.domain.like

interface LikeRepository {

    fun findLikeCountByTarget(likeTarget: LikeTarget): LikeCount?

    fun findLikeCounts(
        targetType: LikeTargetType,
        targetIds: List<Long>,
    ): List<LikeCount>
}
