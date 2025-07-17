package com.loopers.domain.user

class UserCommand {

    data class Create(val userId: String, val email: String, val birthdate: String, val gender: Gender) {
        init {
            require(userId.isNotBlank()) { "User ID cannot be blank." }
            require(email.isNotBlank()) { "Email cannot be blank." }
            require(birthdate.isNotBlank()) { "Birthdate cannot be blank." }
        }
    }
}
