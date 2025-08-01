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
    name = "likes",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["user_id", "target_id", "target_type"],
        ),
    ],
)
class Like(userId: Long, target: LikeTarget) : BaseEntity() {

    val userId: Long = userId

    @Embedded
    val target: LikeTarget = target

    companion object {
        operator fun invoke(userId: Long, targetId: Long, targetType: LikeTargetType): Like {
            require(userId > 0) {
                throw CoreException(BAD_REQUEST, "유효하지 않은 사용자 ID 입니다.")
            }

            return Like(
                userId = userId,
                target = LikeTarget(id = targetId, type = targetType),
            )
        }
    }
}
