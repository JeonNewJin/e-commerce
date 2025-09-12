package com.loopers.domain.metrics.collector

import com.loopers.domain.metrics.CollectMethod
import com.loopers.domain.metrics.CollectMethod.PRODUCT_LIKED
import com.loopers.domain.metrics.ProductMetrics
import com.loopers.domain.metrics.ProductMetricsCommand
import com.loopers.domain.metrics.ProductMetricsProcessor
import com.loopers.domain.metrics.ProductMetricsRepository
import com.loopers.domain.metrics.ProductRankingRepository
import com.loopers.domain.metrics.ProductRankingScoreCalculator
import com.loopers.domain.metrics.ProductRankingWeight
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class ProductLikedCollector(
    private val productMetricsRepository: ProductMetricsRepository,
    private val productRankingRepository: ProductRankingRepository,
) : ProductMetricsProcessor {

    override fun collectMethod(): CollectMethod = PRODUCT_LIKED

    @Transactional
    override fun process(command: List<ProductMetricsCommand.Collect>) {
        val productIdCountMap = command.groupingBy { it.productId }.eachCount()

        productIdCountMap.forEach { (productId, count) ->
            val productMetrics = productMetricsRepository.findByProductIdAndDate(productId, LocalDate.now())
                ?: ProductMetrics.create(productId, LocalDate.now())

            productMetrics.addLikeCount(count.toLong())
            productMetricsRepository.save(productMetrics)

            ProductRankingScoreCalculator.calculateScore(ProductRankingWeight.LIKE)
        }
    }
}
