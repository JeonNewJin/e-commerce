package com.loopers.domain.user.model

import com.loopers.domain.user.entity.User
import kotlin.Long
import kotlin.String

data class UserInfo(
    val id: Long,
    val loginId: String,
    val email: String,
    val birthdate: String,
    val gender: Gender,
) {
    companion object {
        fun from(user: User): UserInfo =
            UserInfo(
                id = user.id,
                loginId = user.loginId.value,
                email = user.email.value,
                birthdate = user.birthdate.value,
                gender = user.gender,
            )
    }
}
