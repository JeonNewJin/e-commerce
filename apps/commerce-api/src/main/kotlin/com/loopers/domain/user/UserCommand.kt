package com.loopers.domain.user

import kotlin.String

class UserCommand private constructor() {

    data class Register(val loginId: String, val email: String, val birthdate: kotlin.String, val gender: Gender)
}
