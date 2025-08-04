package com.loopers.application.user

import com.loopers.domain.user.Gender
import com.loopers.domain.user.UserInfo

data class UserOutput(val loginId: String, val email: String, val birthdate: String, val gender: Gender) {
    companion object {
        fun from(info: UserInfo): UserOutput =
            UserOutput(
                loginId = info.loginId,
                email = info.email,
                birthdate = info.birthdate,
                gender = info.gender,
            )
    }
}
