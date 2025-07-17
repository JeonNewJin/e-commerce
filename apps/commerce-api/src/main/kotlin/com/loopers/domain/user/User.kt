package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "member")
class User(userId: String, email: String, birthdate: LocalDate, gender: Gender) : BaseEntity() {

    @Column(name = "user_id", nullable = false)
    val userId = userId

    @Column(name = "email", nullable = false)
    var email: String = email
        private set

    @Column(name = "birthdate", nullable = false)
    var birthdate: LocalDate = birthdate
        private set

    @Enumerated(STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender = gender
        private set

    companion object {
        operator fun invoke(
            userId: String,
            email: String,
            birthdate: String,
            gender: Gender,
        ): User {
            validateUserId(userId)
            validateEmail(email)
            validateBirthdate(birthdate)

            return User(
                userId = userId,
                email = email,
                birthdate = LocalDate.parse(birthdate),
                gender = gender,
            )
        }

        private val ID_PATTERN = Regex("^[a-zA-Z0-9]{1,10}$")
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9._%+-]+@([A-Za-z0-9]+(-[A-Za-z0-9]+)*\\.)+[A-Za-z]{2,}$")
        private val BIRTHDATE_PATTERN = Regex("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")

        private fun validateUserId(userId: String) {
            require(ID_PATTERN.matches(userId)) {
                throw CoreException(BAD_REQUEST, "아이디는 영문 및 숫자 10자 이내여야 합니다.")
            }
        }

        private fun validateEmail(email: String) {
            require(EMAIL_PATTERN.matches(email)) {
                throw CoreException(BAD_REQUEST, "이메일 형식이 올바르지 않습니다.")
            }
        }

        private fun validateBirthdate(birthdate: String) {
            require(BIRTHDATE_PATTERN.matches(birthdate)) {
                throw CoreException(BAD_REQUEST, "생년월일 형식이 올바르지 않습니다.")
            }

            val parsedDate = runCatching {
                LocalDate.parse(birthdate)
            }.getOrElse { throw CoreException(BAD_REQUEST, "생년월일 형식이 올바르지 않습니다.") }

            require(parsedDate.isBefore(LocalDate.now())) {
                throw CoreException(BAD_REQUEST, "생년월일은 오늘 이전 날짜여야 합니다.")
            }
        }
    }
}
