package com.loopers.domain.like

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated

@Embeddable
data class LikeTarget(
    @Column(name = "target_id")
    val id: Long,

    @Column(name = "target_type")
    @Enumerated(STRING)
    val type: LikeableType,
) {

    init {
        require(id > 0) {
            throw CoreException(BAD_REQUEST, "유효하지 않은 대상 ID 입니다.")
        }
    }
}
