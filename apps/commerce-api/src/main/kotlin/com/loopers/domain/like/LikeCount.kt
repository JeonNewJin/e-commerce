package com.loopers.domain.like

import com.loopers.domain.BaseEntity
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
class LikeCount private constructor(target: LikeTarget, count: Long) : BaseEntity() {

    @Embedded
    val target: LikeTarget = target

    var count: Long = count
        private set

    companion object {
        operator fun invoke(targetId: Long, targetType: LikeTargetType, count: Long = 0L): LikeCount {
            require(count >= 0) {
                throw CoreException(BAD_REQUEST, "좋아요 카운트는 0 이상이어야 합니다.")
            }

            return LikeCount(
                LikeTarget(id = targetId, type = targetType),
                count,
            )
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
