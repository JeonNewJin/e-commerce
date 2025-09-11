package com.loopers.domain.metrics

interface ProductMetricsProcessor {

    fun collectMethod(): CollectMethod

    fun process(command: ProductMetricsCommand.Collect)
}
