package com.loopers.domain.like

import com.loopers.domain.like.model.LikeableType

object LikeEvent {

    data class LikeCreated(val userId: Long, val targetType: LikeableType, val targetId: Long)

    data class LikeDeleted(val userId: Long, val targetType: LikeableType, val targetId: Long)
}
