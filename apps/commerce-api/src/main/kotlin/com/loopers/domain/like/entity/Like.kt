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
    name = "likes",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["user_id", "target_id", "target_type"],
        ),
    ],
)
class Like(userId: Long, targetId: Long, targetType: LikeableType) : BaseEntity() {

    val userId: Long = userId

    @Embedded
    val target: LikeTarget = LikeTarget(id = targetId, type = targetType)

    init {
        require(userId > 0) {
            throw CoreException(BAD_REQUEST, "유효하지 않은 사용자 ID 입니다.")
        }
    }
}
