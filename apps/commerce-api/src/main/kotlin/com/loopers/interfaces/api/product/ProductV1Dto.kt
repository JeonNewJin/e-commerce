package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductOutput
import com.loopers.application.product.ProductsOutput
import com.loopers.domain.product.model.ProductStatus
import java.math.BigDecimal

class ProductV1Dto private constructor() {

    class Response {

        data class ProductResponse(
            val id: Long,
            val name: String,
            val price: BigDecimal,
            val publishedAt: String,
            val status: ProductStatus,
            val likeCount: Long,
            val brandId: Long,
            val brandName: String,
            val brandDescription: String,
        ) {
            companion object {
                fun from(output: ProductOutput): ProductResponse =
                    ProductResponse(
                        id = output.id,
                        name = output.name,
                        price = output.price,
                        publishedAt = output.publishedAt,
                        status = output.status,
                        likeCount = output.likeCount,
                        brandId = output.brandId,
                        brandName = output.brandName,
                        brandDescription = output.brandDescription,
                    )
            }
        }

        data class ProductsResponse(
            val products: List<ProductResponse>,
            val totalElements: Long,
            val totalPages: Int,
            val currentPage: Int,
            val pageSize: Int,
            val hasNext: Boolean,
            val hasPrevious: Boolean,
        ) {
            companion object {
                fun from(output: ProductsOutput): ProductsResponse =
                    ProductsResponse(
                        products = output.products.map { ProductResponse.from(it) },
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
