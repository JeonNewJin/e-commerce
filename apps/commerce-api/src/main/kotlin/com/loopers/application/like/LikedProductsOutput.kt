package com.loopers.application.like

import com.loopers.application.product.ProductOutput
import com.loopers.domain.brand.model.BrandInfo
import com.loopers.domain.like.model.LikeInfo
import com.loopers.domain.product.model.ProductInfo
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.data.domain.Page

data class LikedProductsOutput(
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
            likes: Page<LikeInfo>,
            products: List<ProductInfo>,
            brands: List<BrandInfo>,
        ): LikedProductsOutput {
            val productMap = products.associateBy { it.id }
            val brandMap = brands.associateBy { it.id }
            val likeCountMap = likes.associateBy { it.targetId }

            val productItems = likes.content.map { like ->
                val product = productMap[like.targetId] ?: throw CoreException(NOT_FOUND, "해당 상품을 찾을 수 없습니다.")
                val brand = brandMap[product.brandId] ?: throw CoreException(NOT_FOUND, "해당 브랜드를 찾을 수 없습니다.")
                val likeCount = likeCountMap[product.id]?.count ?: 0L

                ProductOutput(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    publishedAt = product.publishedAt,
                    status = product.status,
                    likeCount = likeCount,
                    brandId = brand.id,
                    brandName = brand.name,
                    brandDescription = brand.description,
                )
            }

            return LikedProductsOutput(
                products = productItems,
                totalElements = likes.totalElements,
                totalPages = likes.totalPages,
                currentPage = likes.number,
                pageSize = likes.size,
                hasNext = likes.hasNext(),
                hasPrevious = likes.hasPrevious(),
            )
        }
    }
}
