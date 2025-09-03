package com.loopers.domain.product.entity

import com.loopers.domain.BaseEntity
import com.loopers.domain.product.model.ProductStatus
import com.loopers.domain.product.model.ProductStatus.SALE
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.BAD_REQUEST
import com.loopers.support.error.ErrorType.NOT_FOUND
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
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
class Product(brandId: Long, name: String, price: BigDecimal, publishedAt: String, status: ProductStatus, likeCount: Long = 0L) :
    BaseEntity() {

    val brandId: Long = brandId

    val name: String = name

    var price: BigDecimal = price
        private set

    var publishedAt: String = publishedAt
        private set

    @Enumerated(STRING)
    var status: ProductStatus = status
        private set

    var likeCount: Long = likeCount
        private set

    init {
        require(name.isNotBlank()) {
            throw CoreException(BAD_REQUEST, "상품 이름은 필수입니다.")
        }
        require(price >= BigDecimal.ZERO) {
            throw CoreException(BAD_REQUEST, "상품 가격은 0 이상이어야 합니다.")
        }
        require(brandId > 0) {
            throw CoreException(BAD_REQUEST, "유효하지 않은 브랜드 ID 입니다.")
        }
        require(publishedAt.isNotBlank()) {
            throw CoreException(BAD_REQUEST, "상품 출간일시는 필수입니다.")
        }
    }

    fun checkIsOnSale() {
        require(status == SALE) {
            throw CoreException(NOT_FOUND, "해당 상품은 판매 중이 아닙니다.")
        }
    }

    fun increaseLikeCount() {
        likeCount++
    }

    fun decreaseLikeCount() {
        if (likeCount > 0) {
            likeCount--
        }
    }
}
