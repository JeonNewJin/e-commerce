package com.loopers.domain.user

import com.loopers.domain.user.entity.User
import com.loopers.domain.user.vo.LoginId

interface UserRepository {

    fun save(user: User)

    fun findByLoginId(loginId: LoginId): User?
}
