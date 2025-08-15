package com.loopers.domain.product.model

import com.loopers.domain.product.entity.Product
import java.math.BigDecimal

data class ProductInfo(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val publishedAt: String,
    val status: ProductStatus,
    val likeCount: Long,
    val brandId: Long,
) {
    companion object {
        fun from(product: Product): ProductInfo =
            ProductInfo(
                id = product.id,
                name = product.name,
                price = product.price,
                publishedAt = product.publishedAt,
                status = product.status,
                likeCount = product.likeCount,
                brandId = product.brandId,
            )
    }
}
