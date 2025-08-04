package com.loopers.domain.user

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class LoginId(
    @Column(name = "login_id")
    val value: String,
) {
    init {
        require(ID_PATTERN.matches(value)) {
            throw CoreException(BAD_REQUEST, "아이디는 영문 및 숫자 10자 이내여야 합니다.")
        }
    }

    companion object {
        private val ID_PATTERN = Regex("^[a-zA-Z0-9]{1,10}$")
    }
}
