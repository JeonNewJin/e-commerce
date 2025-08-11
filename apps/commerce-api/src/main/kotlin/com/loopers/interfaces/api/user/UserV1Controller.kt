package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.domain.user.UserService
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserV1Controller(private val userFacade: UserFacade, private val userService: UserService) : UserV1ApiSpec {

    @PostMapping
    @ResponseStatus(CREATED)
    override fun signUp(
        @Valid @RequestBody request: UserV1Dto.Request.SignUp,
    ): ApiResponse<UserV1Dto.Response.UserResponse> = userFacade.signUp(request.toInput())
        .let { UserV1Dto.Response.UserResponse.from(it) }
        .let { ApiResponse.success(it) }

    @GetMapping("/me")
    override fun me(
        @RequestHeader("X-USER-ID", required = true) userId: String,
    ): ApiResponse<UserV1Dto.Response.UserResponse> = userService.getUser(userId)
        .let { UserV1Dto.Response.UserResponse.from(it) }
        .let { ApiResponse.success(it) }
}
