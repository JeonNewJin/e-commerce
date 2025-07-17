package com.loopers.application.user

import com.loopers.domain.user.Gender
import com.loopers.domain.user.User
import java.time.LocalDate

data class UserInfo(
    val userId: String,
    val email: String,
    val birthdate: LocalDate,
    val gender: Gender,
) {
    companion object {
        fun from(user: User): UserInfo =
            UserInfo(
                userId = user.userId,
                email = user.email,
                birthdate = user.birthdate,
                gender = user.gender,
            )
    }
}
