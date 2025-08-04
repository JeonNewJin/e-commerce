package com.loopers.domain.like

import org.springframework.data.domain.Page

interface LikeRepository {

    fun findByUserIdAndTarget(
        userId: Long,
        likeTarget: LikeTarget,
    ): Like?

    fun save(like: Like)

    fun saveLikeCount(likeCount: LikeCount)

    fun delete(like: Like)

    fun findLikeCountByTarget(likeTarget: LikeTarget): LikeCount?

    fun findLikeCounts(
        targetType: LikeTargetType,
        targetIds: List<Long>,
    ): List<LikeCount>

    fun findLikes(command: LikeCommand.GetLikes): Page<Like>
}
