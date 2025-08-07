package com.loopers.domain.like.model

import com.loopers.domain.like.entity.LikeCount

data class LikeCountInfo(val targetType: LikeableType, val targetId: Long, val count: Long) {
    companion object {
        fun from(likeCount: LikeCount): LikeCountInfo =
            LikeCountInfo(
                targetType = likeCount.target.type,
                targetId = likeCount.target.id,
                count = likeCount.count,
            )

        fun empty(targetType: LikeableType, targetId: Long): LikeCountInfo =
            LikeCountInfo(
                targetType = targetType,
                targetId = targetId,
                count = 0,
            )
    }
}
