package com.loopers.domain.product.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.product.model.ProductStatus
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(
    name = "product",
    indexes = [
        Index(name = "idx_brand_status", columnList = "brandId, status"),
        Index(name = "idx_brand_status_published_at", columnList = "brandId, status, publishedAt DESC"),
        Index(name = "idx_brand_status_price", columnList = "brandId, status, price"),
        Index(name = "idx_brand_status_like_count", columnList = "brandId, status, likeCount DESC"),
        Index(name = "idx_status_published_at", columnList = "status, publishedAt DESC"),
        Index(name = "idx_status_price", columnList = "status, price"),
        Index(name = "idx_status_like_count", columnList = "status, likeCount DESC"),
    ],
)
class Product(name: String, price: BigDecimal, brandId: Long, publishedAt: String, status: ProductStatus) : BaseEntity() {

    val brandId: Long = brandId

    val name: String = name

    var price: BigDecimal = price
        private set

    var publishedAt: String = publishedAt
        private set

    @Enumerated(EnumType.STRING)
    var status: ProductStatus = status
        private set

    var likeCount: Long = 0L
        private set

    init {
        require(name.isNotBlank()) {
            throw CoreException(ErrorType.BAD_REQUEST, "상품 이름은 필수입니다.")
        }
        require(price >= BigDecimal.ZERO) {
            throw CoreException(ErrorType.BAD_REQUEST, "상품 가격은 0 이상이어야 합니다.")
        }
        require(brandId > 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 브랜드 ID 입니다.")
        }
        require(publishedAt.isNotBlank()) {
            throw CoreException(ErrorType.BAD_REQUEST, "상품 출간일시는 필수입니다.")
        }
        requireNotNull(status) {
            throw CoreException(ErrorType.BAD_REQUEST, "상품 상태는 필수입니다.")
        }
    }

    fun isNotOnSale(): Boolean = status != ProductStatus.SALE
}
