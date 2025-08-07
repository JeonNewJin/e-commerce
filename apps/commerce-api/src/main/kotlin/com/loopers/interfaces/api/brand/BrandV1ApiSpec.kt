package com.loopers.interfaces.api.brand

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "Brand V1 API", description = "브랜드 API 입니다.")
interface BrandV1ApiSpec {

    @Operation(
        summary = "브랜드 정보 조회",
        description = "브랜드 정보 조회에 성공할 경우, 해당하는 브랜드 정보를 응답으로 반환합니다.",
    )
    fun getBrand(
        @Parameter(description = "브랜드 ID", required = true)
        @PathVariable brandId: Long,
    ): ApiResponse<BrandV1Dto.Response.BrandResponse>
}
