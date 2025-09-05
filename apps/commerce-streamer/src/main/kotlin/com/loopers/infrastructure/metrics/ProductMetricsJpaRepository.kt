package com.loopers.infrastructure.metrics

import com.loopers.domain.metrics.ProductMetrics
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ProductMetricsJpaRepository : JpaRepository<ProductMetrics, Long> {

    fun findByProductIdAndDate(productId: Long, date: LocalDate): ProductMetrics?
}
