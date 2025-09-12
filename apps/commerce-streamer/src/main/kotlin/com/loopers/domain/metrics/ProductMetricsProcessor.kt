package com.loopers.domain.metrics

interface ProductMetricsProcessor {

    fun collectMethod(): CollectMethod

    fun process(command: List<ProductMetricsCommand.Collect>)
}
