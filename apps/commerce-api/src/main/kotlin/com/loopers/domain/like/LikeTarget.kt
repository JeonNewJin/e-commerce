package com.loopers.domain.like

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated

@Embeddable
data class LikeTarget private constructor(
    @Column(name = "target_id")
    val id: Long,

    @Column(name = "target_type")
    @Enumerated(STRING)
    val type: LikeTargetType,
) {

    companion object {
        operator fun invoke(id: Long, type: LikeTargetType): LikeTarget {
            require(id > 0) {
                throw CoreException(BAD_REQUEST, "유효하지 않은 대상 ID 입니다.")
            }

            return LikeTarget(id = id, type = type)
        }
    }
}
