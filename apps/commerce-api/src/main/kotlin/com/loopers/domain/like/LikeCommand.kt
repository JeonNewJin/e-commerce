package com.loopers.domain.like

import org.springframework.data.domain.Pageable

class LikeCommand private constructor() {

    data class Like(val userId: Long, val targetType: LikeableType, val targetId: Long)

    data class Unlike(val userId: Long, val targetType: LikeableType, val targetId: Long)

    data class GetLikes(val userId: Long, val targetType: LikeableType, val pageable: Pageable)
}
