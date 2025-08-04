package com.loopers.application.point

import com.loopers.domain.point.PointWalletService
import com.loopers.domain.user.LoginId
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PointWalletFacade(private val userService: UserService, private val pointWalletService: PointWalletService) {

    @Transactional(readOnly = true)
    fun getPoint(loginId: String): PointWalletOutput {
        val user = userService.getUser(LoginId(loginId))

        return pointWalletService.getPointWallet(user.id)
            .let { PointWalletOutput.from(it) }
    }

    @Transactional
    fun charge(input: PointWalletInput.Charge): PointWalletOutput {
        val user = userService.getUser(LoginId(input.loginId))

        return pointWalletService.charge(input.toCommand(user.id))
            .let { PointWalletOutput.from(it) }
    }
}
