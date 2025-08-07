package com.loopers.infrastructure.like

import com.loopers.domain.like.entity.LikeCount
import com.loopers.domain.like.model.LikeableType
import com.loopers.domain.like.vo.LikeTarget
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LikeCountJpaRepository : JpaRepository<LikeCount, Long> {

    fun findByTarget(likeTarget: LikeTarget): LikeCount?

    @Query("SELECT lc FROM LikeCount lc WHERE lc.target.type = :targetType AND lc.target.id IN :targetIds")
    fun findByTargetTypeAndTargetIds(targetType: LikeableType, targetIds: List<Long>): List<LikeCount>
}
