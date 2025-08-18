package com.loopers.interfaces.api.product

import com.loopers.domain.product.model.ProductSortType
import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "Product V1 API", description = "상품 API 입니다.")
interface ProductV1ApiSpec {

    @Operation(
        summary = "상품 정보 조회",
        description = "상품 정보 조회에 성공할 경우, 해당하는 상품 정보를 응답으로 반환합니다.",
    )
    fun getProduct(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable productId: Long,
    ): ApiResponse<ProductV1Dto.Response.ProductResponse>

    @Operation(
        summary = "상품 목록 조회",
        description = "상품 목록 조회에 성공할 경우, 해당하는 상품 목록을 응답으로 반환합니다.",
    )
    fun getProducts(
        @Parameter(description = "상품 목록 조회 요청 정보", required = true)
        @PageableDefault(size = 20, page = 0) pageable: Pageable,
        brandId: Long? = null,
        sort: ProductSortType? = null,
    ): ApiResponse<ProductV1Dto.Response.ProductsResponse>
}
