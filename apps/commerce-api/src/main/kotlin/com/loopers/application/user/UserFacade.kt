package com.loopers.application.user

import com.loopers.domain.user.UserCommand
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserFacade(private val userService: UserService) {

    @Transactional
    fun signup(command: UserCommand.Create): UserInfo {
        userService.find(command.userId)
            ?.let { throw CoreException(CONFLICT, "동일한 ID로 이미 가입된 계정이 존재합니다.") }

        return userService.create(command)
            .let { UserInfo.from(it) }
    }
}
