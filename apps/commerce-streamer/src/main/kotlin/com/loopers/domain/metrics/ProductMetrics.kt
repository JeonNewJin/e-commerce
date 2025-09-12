package com.loopers.domain.metrics

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "product_metrics")
class ProductMetrics(productId: Long, date: LocalDate, likeCount: Long, viewCount: Long, salesCount: Long) : BaseEntity() {

    val productId: Long = productId

    val date: LocalDate = date

    var likeCount: Long = likeCount
        private set

    var viewCount: Long = viewCount
        private set

    var salesCount: Long = salesCount
        private set

    companion object {
        fun create(productId: Long, date: LocalDate) =
            ProductMetrics(
                productId = productId,
                date = date,
                likeCount = 0L,
                viewCount = 0L,
                salesCount = 0L,
            )
    }

    fun addLikeCount(count: Long) {
        likeCount += count
    }

    fun subtractLikeCount(count: Long) {
        likeCount = (likeCount - count).coerceAtLeast(0)
    }

    fun addViewCount(count: Long) {
        viewCount += count
    }

    fun addSalesCount(count: Long) {
        salesCount += count
    }
}
