package com.loopers.application.like

import com.loopers.domain.like.LikeCommand
import com.loopers.domain.like.model.LikeableType
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

    data class FindLikes(val loginId: String, val targetType: LikeableType, val pageable: Pageable) {
        fun toCommand(userId: Long): LikeCommand.FindLikes =
            LikeCommand.FindLikes(
                userId = userId,
                targetType = targetType,
                pageable = pageable,
            )
    }
}
