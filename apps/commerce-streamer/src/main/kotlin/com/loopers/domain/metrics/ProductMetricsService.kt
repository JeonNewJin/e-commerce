package com.loopers.domain.metrics

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.collections.associateBy

@Service
class ProductMetricsService(productMetricsProcessors: List<ProductMetricsProcessor>) {

    private val productMetricsProcessorMap by lazy { productMetricsProcessors.associateBy { it.collectMethod() } }

    @Transactional
    fun collect(command: ProductMetricsCommand.Collect) {
        val handler = productMetricsProcessorMap[command.eventType]
            ?: throw IllegalStateException("지원하지 않는 이벤트 타입입니다: ${command.eventType}")

        handler.process(command)
    }
}
