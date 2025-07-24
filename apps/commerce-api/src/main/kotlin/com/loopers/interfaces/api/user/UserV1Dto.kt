package com.loopers.interfaces.api.user

import com.loopers.application.user.UserInfo
import com.loopers.domain.user.Gender
import com.loopers.domain.user.UserCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

object UserV1Dto {

    class Request {
        data class Signup(
            @Schema(description = "아이디", example = "wjsyuwls")
            @field:NotNull
            val userId: String,

            @Schema(description = "이메일", example = "wjsyuwls@gmail.com")
            @field:NotNull
            val email: String,

            @Schema(description = "생년월일", example = "2000-01-01")
            @field:NotNull
            val birthdate: String,

            @Schema(description = "성별 (M: 남성, F: 여성)", example = "M")
            @field:NotNull
            val gender: Gender,
        ) {
            fun toCommand(): UserCommand.Create =
                UserCommand.Create(
                    userId = userId,
                    email = email,
                    birthdate = birthdate,
                    gender = gender,
                )
        }
    }

    class Response {
        data class UserResponse(val userId: String, val email: String, val birthdate: String, val gender: Gender) {
            companion object {
                fun from(userInfo: UserInfo): UserResponse =
                    UserResponse(
                        userId = userInfo.userId,
                        email = userInfo.email,
                        birthdate = userInfo.birthdate.toString(),
                        gender = userInfo.gender,
                    )
            }
        }
    }
}
