package com.loopers.domain.like.model

data class LikeInfo(val userId: Long, val targetType: LikeableType, val targetId: Long, val count: Long) {
    companion object {
        fun from(like: LikeWithCount): LikeInfo =
            LikeInfo(
                userId = like.userId,
                targetId = like.targetId,
                targetType = like.targetType,
                count = like.count,
            )
    }
}
