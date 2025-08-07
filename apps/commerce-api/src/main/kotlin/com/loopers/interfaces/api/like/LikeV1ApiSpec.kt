package com.loopers.interfaces.api.like

import com.loopers.domain.like.model.LikeableType
import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "Like V1 API", description = "좋아요 API 입니다.")
interface LikeV1ApiSpec {

    @Operation(
        summary = "좋아요 등록",
        description = "좋아요를 등록합니다.",
    )
    fun like(
        @Parameter(
            name = "X-USER-ID",
            description = "회원가입 때 가입한 ID 입니다.",
            required = true,
            `in` = ParameterIn.HEADER,
        )
        userId: String,

        @Parameter(description = "상품 ID", required = true)
        @PathVariable productId: Long,

        @RequestBody(
            description = "좋아요 등록 파라미터",
            required = true,
            content = [
                Content(
                    schema = Schema(LikeV1Dto.Request.Like::class),
                ),
            ],
        )
        request: LikeV1Dto.Request.Like,
    ): ApiResponse<Unit>

    @Operation(
        summary = "좋아요 취소",
        description = "좋아요를 취소합니다.",
    )
    fun unlike(
        @Parameter(
            name = "X-USER-ID",
            description = "회원가입 때 가입한 ID 입니다.",
            required = true,
            `in` = ParameterIn.HEADER,
        )
        userId: String,

        @Parameter(description = "상품 ID", required = true)
        @PathVariable productId: Long,

        @RequestBody(
            description = "좋아요 취소 파라미터",
            required = true,
            content = [
                Content(
                    schema = Schema(LikeV1Dto.Request.Unlike::class),
                ),
            ],
        )
        request: LikeV1Dto.Request.Unlike,
    ): ApiResponse<Unit>

    @Operation(
        summary = "좋아요 한 상품 목록 조회",
        description = "좋아 한 상품 목록 조회에 성공할 경우, 해당하는 상품 목록을 응답으로 반환합니다.",
    )
    fun getLikedProducts(
        @Parameter(
            name = "X-USER-ID",
            description = "회원가입 때 가입한 ID 입니다.",
            required = true,
            `in` = ParameterIn.HEADER,
        )
        userId: String,
        @PageableDefault(size = 20, page = 0) pageable: Pageable,
        targetType: LikeableType,
    ): ApiResponse<LikeV1Dto.Response.LikedProductsResponse>
}
