package com.loopers.domain.metrics

object ProductMetricsCommand {
    data class Collect(
        val eventId: String,
        val eventType: CollectMethod,
        val productId: Long,
        val userId: Long? = null,
        val quantity: Int? = 0,
    )
}
