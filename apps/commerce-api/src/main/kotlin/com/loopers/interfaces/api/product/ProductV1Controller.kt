package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductFacade
import com.loopers.application.product.ProductInput
import com.loopers.interfaces.api.ApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductV1Controller(private val productFacade: ProductFacade) : ProductV1ApiSpec {

    @GetMapping("/{productId}")
    override fun getProduct(
        @PathVariable productId: Long,
    ): ApiResponse<ProductV1Dto.Response.ProductResponse> =
        productFacade.getProductOnSale(productId)
            .let { ProductV1Dto.Response.ProductResponse.from(it) }
            .let { ApiResponse.success(it) }

    @GetMapping
    override fun getProducts(
        @PageableDefault(size = 20, page = 0) pageable: Pageable,
        brandId: Long?,
    ): ApiResponse<ProductV1Dto.Response.ProductsResponse> =
        productFacade.getProductsOnSale(ProductInput.FindProductsOnSale(brandId = brandId, pageable = pageable))
            .let { ProductV1Dto.Response.ProductsResponse.from(it) }
            .let { ApiResponse.success(it) }
}
