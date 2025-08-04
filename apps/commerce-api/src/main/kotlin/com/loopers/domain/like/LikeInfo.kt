package com.loopers.domain.like

data class LikeInfo(val userId: Long, val targetId: Long, val targetType: LikeableType) {
    companion object {
        fun from(like: Like): LikeInfo =
            LikeInfo(
                userId = like.userId,
                targetId = like.target.id,
                targetType = like.target.type,
            )
    }
}
