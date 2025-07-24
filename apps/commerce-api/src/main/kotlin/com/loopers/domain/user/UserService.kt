package com.loopers.domain.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun create(command: UserCommand.Create): User {
        val user = User(
            userId = command.userId,
            email = command.email,
            birthdate = command.birthdate,
            gender = command.gender,
        )
        userRepository.save(user)
        return user
    }

    @Transactional(readOnly = true)
    fun find(userId: String): User? = userRepository.find(userId)
}
