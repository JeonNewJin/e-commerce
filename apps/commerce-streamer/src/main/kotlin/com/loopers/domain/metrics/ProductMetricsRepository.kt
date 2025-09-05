package com.loopers.domain.metrics

import java.time.LocalDate

interface ProductMetricsRepository {

    fun findByProductIdAndDate(productId: Long, date: LocalDate): ProductMetrics?

    fun save(productMetrics: ProductMetrics)
}
