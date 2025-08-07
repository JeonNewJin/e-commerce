package com.loopers.domain.user

import com.loopers.domain.user.entity.User
import com.loopers.domain.user.model.UserInfo
import com.loopers.domain.user.vo.LoginId
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.CONFLICT
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun register(command: UserCommand.Register): UserInfo {
        userRepository.findByLoginId(LoginId(command.loginId))
            ?.let { throw CoreException(CONFLICT, "동일한 ID로 이미 가입된 계정이 존재합니다.") }

        val user = User(
            loginId = command.loginId,
            email = command.email,
            birthdate = command.birthdate,
            gender = command.gender,
        )
        userRepository.save(user)
        return UserInfo.from(user)
    }

    @Transactional(readOnly = true)
    fun getUser(loginId: LoginId): UserInfo =
        userRepository.findByLoginId(loginId)
            ?.let { UserInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "해당 사용자를 찾을 수 없습니다.")
}
