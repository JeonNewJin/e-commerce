package com.loopers.domain.like.model

data class LikeWithCount(val id: Long, val userId: Long, val targetType: LikeableType, val targetId: Long, val count: Long)
