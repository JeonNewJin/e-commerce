package com.loopers.interfaces.api.like

import com.loopers.application.like.LikeInput
import com.loopers.application.like.LikedProductsOutput
import com.loopers.application.product.ProductOutput
import com.loopers.domain.like.model.LikeableType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class LikeV1Dto private constructor() {

    class Request {

        data class Like(
            @Schema(description = "대상 타입", example = "PRODUCT")
            @field:NotNull
            val targetType: LikeableType,
        ) {
            fun toInput(userId: String, targetId: Long): LikeInput.Like =
                LikeInput.Like(
                    loginId = userId,
                    targetId = targetId,
                    targetType = targetType,
                )
        }

        data class Unlike(
            @Schema(description = "대상 타입", example = "PRODUCT")
            @field:NotNull
            val targetType: LikeableType,
        ) {
            fun toInput(userId: String, targetId: Long): LikeInput.Unlike =
                LikeInput.Unlike(
                    loginId = userId,
                    targetId = targetId,
                    targetType = targetType,
                )
        }
    }

    class Response {

        data class LikedProductsResponse(
            val products: List<ProductOutput>,
            val totalElements: Long,
            val totalPages: Int,
            val currentPage: Int,
            val pageSize: Int,
            val hasNext: Boolean,
            val hasPrevious: Boolean,
        ) {
            companion object {
                fun from(output: LikedProductsOutput): LikedProductsResponse =
                    LikedProductsResponse(
                        products = output.products,
                        totalElements = output.totalElements,
                        totalPages = output.totalPages,
                        currentPage = output.currentPage,
                        pageSize = output.pageSize,
                        hasNext = output.hasNext,
                        hasPrevious = output.hasPrevious,
                    )
            }
        }
    }
}
