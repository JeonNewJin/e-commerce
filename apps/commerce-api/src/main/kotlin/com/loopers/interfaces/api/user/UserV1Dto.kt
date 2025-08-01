package com.loopers.interfaces.api.user

import com.loopers.application.user.UserInput
import com.loopers.application.user.UserOutput
import com.loopers.domain.user.Gender
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

class UserV1Dto private constructor() {

    class Request {

        data class SignUp(
            @Schema(description = "아이디", example = "wjsyuwls")
            @field:Pattern(regexp = "^[a-zA-Z0-9]{6,10}$")
            val userId: String,

            @Schema(description = "이메일", example = "wjsyuwls@gmail.com")
            @field:Email
            val email: String,

            @Schema(description = "생년월일", example = "2000-01-01")
            @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
            val birthdate: String,

            @Schema(description = "성별 (M: 남성, F: 여성)", example = "M")
            @field:NotNull
            val gender: Gender,
        ) {
            fun toInput(): UserInput.Register =
                UserInput.Register(
                    loginId = userId,
                    email = email,
                    birthdate = birthdate,
                    gender = gender,
                )
        }
    }

    class Response {

        data class UserResponse(val userId: String, val email: String, val birthdate: String, val gender: Gender) {
            companion object {
                fun from(output: UserOutput): UserResponse =
                    UserResponse(
                        userId = output.loginId,
                        email = output.email,
                        birthdate = output.birthdate,
                        gender = output.gender,
                    )
            }
        }
    }
}
