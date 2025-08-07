package com.loopers.application.user

import com.loopers.domain.user.model.Gender
import com.loopers.domain.user.UserCommand

class UserInput private constructor() {

    data class Register(val loginId: String, val email: String, val birthdate: String, val gender: Gender) {
        fun toCommand() = UserCommand.Register(
            loginId = loginId,
            email = email,
            birthdate = birthdate,
            gender = gender,
        )
    }
}
