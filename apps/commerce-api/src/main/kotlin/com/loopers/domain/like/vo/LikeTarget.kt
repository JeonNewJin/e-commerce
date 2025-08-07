package com.loopers.domain.like.vo

import com.loopers.domain.like.model.LikeableType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class LikeTarget(
    @Column(name = "target_id")
    val id: Long,

    @Column(name = "target_type")
    @Enumerated(EnumType.STRING)
    val type: LikeableType,
) {

    init {
        require(id > 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 대상 ID 입니다.")
        }
    }
}
