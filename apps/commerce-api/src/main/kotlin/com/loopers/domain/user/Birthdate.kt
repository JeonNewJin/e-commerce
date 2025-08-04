package com.loopers.domain.user

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDate
import kotlin.String

@Embeddable
data class Birthdate private constructor(
    @Column(name = "birthdate")
    val value: kotlin.String,
) {
    companion object {
        private val BIRTHDATE_PATTERN = Regex("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")

        operator fun invoke(value: String): Birthdate {
            require(BIRTHDATE_PATTERN.matches(value)) {
                throw CoreException(BAD_REQUEST, "생년월일 형식이 올바르지 않습니다. (YYYY-MM-DD)")
            }

            val parsedDate = runCatching {
                LocalDate.parse(value)
            }.getOrElse { throw CoreException(BAD_REQUEST, "생년월일 형식이 올바르지 않습니다. (YYYY-MM-DD)") }

            require(parsedDate.isBefore(LocalDate.now())) {
                throw CoreException(BAD_REQUEST, "생년월일은 오늘 이전 날짜여야 합니다.")
            }

            return Birthdate(value)
        }
    }
}
