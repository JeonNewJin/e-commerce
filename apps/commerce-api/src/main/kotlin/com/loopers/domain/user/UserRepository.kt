package com.loopers.domain.user

interface UserRepository {

    fun save(user: User)

    fun find(userId: String): User?
}
