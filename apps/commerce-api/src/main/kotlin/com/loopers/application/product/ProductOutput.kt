package com.loopers.application.product

import com.loopers.domain.brand.model.BrandInfo
import com.loopers.domain.like.model.LikeCountInfo
import com.loopers.domain.product.model.ProductInfo
import com.loopers.domain.product.model.ProductStatus
import java.math.BigDecimal

data class ProductOutput(
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
        fun of(product: ProductInfo, brand: BrandInfo, likeCount: LikeCountInfo?): ProductOutput =
            ProductOutput(
                id = product.id,
                name = product.name,
                price = product.price,
                publishedAt = product.publishedAt,
                status = product.status,
                likeCount = likeCount?.count ?: 0L,
                brandId = brand.id,
                brandName = brand.name,
                brandDescription = brand.description,
            )
    }
}
