package com.loopers.interfaces.api.like

import com.loopers.application.like.LikeFacade
import com.loopers.application.like.LikeInput
import com.loopers.domain.like.model.LikeableType
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/like/products")
class LikeV1Controller(private val likeFacade: LikeFacade) : LikeV1ApiSpec {

    @ResponseStatus(CREATED)
    @PostMapping("/{productId}")
    override fun like(
        @RequestHeader(name = "X-USER-ID") userId: String,
        @PathVariable productId: Long,
        @Valid @RequestBody request: LikeV1Dto.Request.Like,
    ): ApiResponse<Unit> {
        likeFacade.like(request.toInput(userId, productId))
        return ApiResponse.success(Unit)
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{productId}")
    override fun unlike(
        @RequestHeader(name = "X-USER-ID") userId: String,
        @PathVariable productId: Long,
        @Valid @RequestBody request: LikeV1Dto.Request.Unlike,
    ): ApiResponse<Unit> {
        likeFacade.unlike(request.toInput(userId, productId))
        return ApiResponse.success(Unit)
    }

    @GetMapping
    override fun getLikedProducts(
        @RequestHeader(name = "X-USER-ID") userId: String,
        @PageableDefault(size = 20, page = 0) pageable: Pageable,
        @RequestBody targetType: LikeableType,
    ): ApiResponse<LikeV1Dto.Response.LikedProductsResponse> =
        likeFacade.getLikedProducts(LikeInput.FindLikes(userId, targetType, pageable))
            .let { LikeV1Dto.Response.LikedProductsResponse.from(it) }
            .let { ApiResponse.success(it) }
}
