package com.loopers.interfaces.api.user

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "User V1 API", description = "사용자 API 입니다.")
interface UserV1ApiSpec {

    @Operation(
        summary = "회원가입",
        description = "회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환합니다.",
    )
    fun signup(
        @RequestBody(
            description = "회원가입 파라미터",
            required = true,
            content = [
                Content(
                    schema = Schema(UserV1Dto.Request.Signup::class),
                ),
            ],
        )
        request: UserV1Dto.Request.Signup,
    ): ApiResponse<UserV1Dto.Response.UserResponse>
}
