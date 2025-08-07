package com.loopers.domain.user.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import kotlin.String

@Embeddable
data class Email(
    @Column(name = "email")
    val value: String,
) {
    init {
        require(EMAIL_PATTERN.matches(value)) {
            throw CoreException(BAD_REQUEST, "이메일 형식이 올바르지 않습니다.")
        }
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9._%+-]+@([A-Za-z0-9]+(-[A-Za-z0-9]+)*\\.)+[A-Za-z]{2,}$")
    }
}
