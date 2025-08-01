package com.loopers.domain.user

interface UserRepository {

    fun save(user: User)

    fun findByLoginId(loginId: LoginId): User?
}
