package com.loopers.infrastructure.user

import com.loopers.domain.user.User
import com.loopers.domain.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserCoreRepository(private val userJpaRepository: UserJpaRepository) : UserRepository {

    override fun save(user: User) {
        userJpaRepository.save(user)
    }

    override fun find(userId: String): User? = userJpaRepository.findByUserId(userId)
}
