package com.loopers.application.user

import com.loopers.domain.point.PointWalletService
import com.loopers.domain.user.LoginId
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserFacade(private val userService: UserService, private val pointWalletService: PointWalletService) {

    @Transactional
    fun signUp(input: UserInput.Register): UserOutput {
        val user = userService.register(input.toCommand())

        pointWalletService.create(user.id)

        return UserOutput.from(user)
    }

    @Transactional(readOnly = true)
    fun getUser(loginId: kotlin.String): UserOutput =
        userService.getUser(LoginId(loginId))
            .let { UserOutput.from(it) }
}
