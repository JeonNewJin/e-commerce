package com.loopers.domain.metrics.collector

import com.loopers.domain.metrics.CollectMethod
import com.loopers.domain.metrics.ProductMetricsCommand
import com.loopers.domain.metrics.ProductMetricsProcessor
import com.loopers.domain.metrics.ProductMetricsRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ProductUnlikedCollector(private val productMetricsRepository: ProductMetricsRepository) : ProductMetricsProcessor {

    override fun collectMethod(): CollectMethod = CollectMethod.PRODUCT_UNLIKED

    @Transactional
    override fun process(command: ProductMetricsCommand.Collect) {
        val productMetrics = productMetricsRepository.findByProductIdAndDate(command.productId, LocalDate.now())
            ?: return

        productMetrics.decreaseLikeCount()
        productMetricsRepository.save(productMetrics)
    }
}
