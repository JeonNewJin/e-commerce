package com.loopers.infrastructure.like

import com.loopers.domain.like.entity.Like
import com.loopers.domain.like.vo.LikeTarget
import org.springframework.data.jpa.repository.JpaRepository

interface LikeJpaRepository : JpaRepository<Like, Long> {

    fun findByUserIdAndTarget(userId: Long, likeTarget: LikeTarget): Like?
}
