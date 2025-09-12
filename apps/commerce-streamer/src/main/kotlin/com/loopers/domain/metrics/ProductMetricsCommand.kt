package com.loopers.domain.metrics

import java.math.BigDecimal

object ProductMetricsCommand {
    data class Collect(
        val eventId: String,
        val eventType: CollectMethod,
        val productId: Long,
        var price: BigDecimal,
        var amount: Long,
        val quantity: Int? = 0,
    )
}
