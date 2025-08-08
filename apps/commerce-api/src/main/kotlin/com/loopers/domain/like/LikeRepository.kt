package com.loopers.domain.like

import com.loopers.domain.like.entity.Like
import com.loopers.domain.like.entity.LikeCount
import com.loopers.domain.like.model.LikeWithCount
import com.loopers.domain.like.model.LikeableType
import com.loopers.domain.like.vo.LikeTarget
import org.springframework.data.domain.Page

interface LikeRepository {

    fun findByUserIdAndTarget(
        userId: Long,
        likeTarget: LikeTarget,
    ): Like?

    fun save(like: Like)

    fun delete(like: Like)

    fun saveLikeCount(likeCount: LikeCount)

    fun findLikeCountByTarget(likeTarget: LikeTarget): LikeCount?

    fun findLikeCounts(
        targetType: LikeableType,
        targetIds: List<Long>,
    ): List<LikeCount>

    fun findLikes(command: LikeCommand.FindLikes): Page<LikeWithCount>

    fun findLikeCountByTargetWithLock(likeTarget: LikeTarget): LikeCount?
}
