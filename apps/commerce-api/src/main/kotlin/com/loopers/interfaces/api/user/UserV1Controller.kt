package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserV1Controller(private val userFacade: UserFacade) : UserV1ApiSpec {

    @PostMapping
    @ResponseStatus(CREATED)
    override fun signup(
        @Valid @RequestBody request: UserV1Dto.Request.Signup,
    ): ApiResponse<UserV1Dto.Response.UserResponse> = userFacade.signup(request.toCommand())
        .let { UserV1Dto.Response.UserResponse.from(it) }
        .let { ApiResponse.success(it) }
}
