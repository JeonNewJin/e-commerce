package com.loopers.application.product

import com.loopers.domain.brand.model.BrandInfo
import com.loopers.domain.like.model.LikeCountInfo
import com.loopers.domain.product.model.ProductInfo
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.data.domain.Page

data class ProductsOutput(
    val products: List<ProductOutput>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
) {
    companion object {
        fun of(
            products: Page<ProductInfo>,
            brands: List<BrandInfo>,
            likeCounts: List<LikeCountInfo>,
        ): ProductsOutput {
            val brandMap = brands.associateBy { it.id }
            val likeCountMap = likeCounts.associateBy { it.targetId }

            val productItems = products.content.map {
                val brand = brandMap[it.brandId] ?: throw CoreException(NOT_FOUND, "해당 브랜드를 찾을 수 없습니다.")
                val likeCount = likeCountMap[it.id]?.count ?: 0L

                ProductOutput(
                    id = it.id,
                    name = it.name,
                    price = it.price,
                    publishedAt = it.publishedAt,
                    status = it.status,
                    likeCount = likeCount,
                    brandId = brand.id,
                    brandName = brand.name,
                    brandDescription = brand.description,
                )
            }

            return ProductsOutput(
                products = productItems,
                totalElements = products.totalElements,
                totalPages = products.totalPages,
                currentPage = products.number,
                pageSize = products.size,
                hasNext = products.hasNext(),
                hasPrevious = products.hasPrevious(),
            )
        }
    }
}
