package com.loopers.domain.metrics

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ProductMetricsService(productMetricsProcessors: List<ProductMetricsProcessor>) {

    private val productMetricsProcessorMap by lazy { productMetricsProcessors.associateBy { it.collectMethod() } }

    @Transactional
    fun collect(commands: List<ProductMetricsCommand.Collect>) {
        val groupBy = commands.groupBy { it.eventType }

        groupBy.forEach { (eventType, group) ->
            val handler = productMetricsProcessorMap[eventType]
                ?: throw IllegalStateException("지원하지 않는 이벤트 타입입니다: $eventType")

            handler.process(group)
        }
    }
}
