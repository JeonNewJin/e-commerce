package com.loopers.domain.like.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.like.vo.LikeTarget
import com.loopers.domain.like.model.LikeableType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "like_count",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["target_id", "target_type"],
        ),
    ],
)
class LikeCount(targetId: Long, targetType: LikeableType, count: Long = 0L) : BaseEntity() {

    @Embedded
    val target: LikeTarget = LikeTarget(id = targetId, type = targetType)

    var count: Long = count
        private set

    init {
        require(count >= 0) {
            throw CoreException(BAD_REQUEST, "좋아요 카운트는 0 이상이어야 합니다.")
        }
    }

    fun increase() {
        count++
    }

    fun decrease() {
        if (count > 0) {
            count--
        }
    }
}
