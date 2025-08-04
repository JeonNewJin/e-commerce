package com.loopers.domain.like

data class LikeCountInfo(val targetType: LikeableType, val targetId: Long, val count: Long) {
    companion object {
        fun from(likeCount: LikeCount): LikeCountInfo =
            LikeCountInfo(
                targetType = likeCount.target.type,
                targetId = likeCount.target.id,
                count = likeCount.count,
            )
    }
}
