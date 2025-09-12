package com.loopers.domain.metrics.collector

import com.loopers.domain.metrics.CollectMethod
import com.loopers.domain.metrics.CollectMethod.STOCK_ADJUSTED
import com.loopers.domain.metrics.ProductMetrics
import com.loopers.domain.metrics.ProductMetricsCommand
import com.loopers.domain.metrics.ProductMetricsProcessor
import com.loopers.domain.metrics.ProductMetricsRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class StockAdjustedCollector(private val productMetricsRepository: ProductMetricsRepository) : ProductMetricsProcessor {

    override fun collectMethod(): CollectMethod = STOCK_ADJUSTED

    @Transactional
    override fun process(command: List<ProductMetricsCommand.Collect>) {
        val productIdQuantityMap = command.groupBy { it.productId }
            .mapValues { entry -> entry.value.sumOf { it.quantity ?: 0 } }

        productIdQuantityMap.forEach { (productId, quantity) ->
            val productMetrics = productMetricsRepository.findByProductIdAndDate(productId, LocalDate.now())
                ?: ProductMetrics.create(productId, LocalDate.now())

            productMetrics.addSalesCount(quantity.toLong())
            productMetricsRepository.save(productMetrics)
        }
    }
}
