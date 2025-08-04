package com.loopers.application.like

import com.loopers.domain.like.LikeCommand
import com.loopers.domain.like.LikeableType
import org.springframework.data.domain.Pageable

class LikeInput private constructor() {

    data class Like(val loginId: String, val targetType: LikeableType, val targetId: Long) {
        fun toCommand(userId: Long): LikeCommand.Like =
            LikeCommand.Like(
                userId = userId,
                targetType = targetType,
                targetId = targetId,
            )
    }

    data class Unlike(val loginId: String, val targetType: LikeableType, val targetId: Long) {
        fun toCommand(userId: Long): LikeCommand.Unlike =
            LikeCommand.Unlike(
                userId = userId,
                targetType = targetType,
                targetId = targetId,
            )
    }

    data class GetLikes(val loginId: String, val targetType: LikeableType, val pageable: Pageable) {
        fun toCommand(userId: Long): LikeCommand.GetLikes =
            LikeCommand.GetLikes(
                userId = userId,
                targetType = targetType,
                pageable = pageable,
            )
    }
}
