package com.loopers.interfaces.api.brand

import com.loopers.domain.brand.BrandService
import com.loopers.interfaces.api.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/brands")
class BrandV1Controller(private val brandService: BrandService) : BrandV1ApiSpec {

    @GetMapping("/{brandId}")
    override fun getBrand(
        @PathVariable brandId: Long,
    ): ApiResponse<BrandV1Dto.Response.BrandResponse> =
        brandService.getBrand(brandId)
            .let { BrandV1Dto.Response.BrandResponse.from(it) }
            .let { ApiResponse.success(it) }
}
