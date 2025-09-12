package com.loopers.domain.metrics.collector

import com.loopers.domain.metrics.CollectMethod
import com.loopers.domain.metrics.CollectMethod.PRODUCT_UNLIKED
import com.loopers.domain.metrics.ProductMetricsCommand
import com.loopers.domain.metrics.ProductMetricsProcessor
import com.loopers.domain.metrics.ProductMetricsRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ProductUnlikedCollector(private val productMetricsRepository: ProductMetricsRepository) : ProductMetricsProcessor {

    override fun collectMethod(): CollectMethod = PRODUCT_UNLIKED

    @Transactional
    override fun process(command: List<ProductMetricsCommand.Collect>) {
        val productIdCountMap = command.groupingBy { it.productId }.eachCount()

        productIdCountMap.forEach { (productId, count) ->
            val productMetrics = productMetricsRepository.findByProductIdAndDate(productId, LocalDate.now())
                ?: return

            productMetrics.subtractLikeCount(count.toLong())
            productMetricsRepository.save(productMetrics)
        }
    }
}
