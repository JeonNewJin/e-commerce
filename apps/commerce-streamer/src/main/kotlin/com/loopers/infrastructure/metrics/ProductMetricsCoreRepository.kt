package com.loopers.infrastructure.metrics

import com.loopers.domain.metrics.ProductMetrics
import com.loopers.domain.metrics.ProductMetricsRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProductMetricsCoreRepository(private val productMetricsJpaRepository: ProductMetricsJpaRepository) :
    ProductMetricsRepository {

    override fun findByProductIdAndDate(
        productId: Long,
        date: LocalDate,
    ): ProductMetrics? = productMetricsJpaRepository.findByProductIdAndDate(productId, date)

    override fun save(productMetrics: ProductMetrics) {
        productMetricsJpaRepository.save(productMetrics)
    }
}
