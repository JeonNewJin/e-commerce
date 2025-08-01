package com.loopers.domain.like

import org.springframework.data.domain.Pageable

class LikeCommand private constructor() {

    data class Like(val userId: Long, val targetType: LikeTargetType, val targetId: Long)

    data class Unlike(val userId: Long, val targetType: LikeTargetType, val targetId: Long)

    data class GetLikes(val userId: Long, val targetType: LikeTargetType, val pageable: Pageable)
}
