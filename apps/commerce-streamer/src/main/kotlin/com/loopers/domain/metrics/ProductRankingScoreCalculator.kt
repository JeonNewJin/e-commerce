package com.loopers.domain.metrics

import com.loopers.domain.metrics.ProductRankingWeight.LIKE
import com.loopers.domain.metrics.ProductRankingWeight.SALE
import com.loopers.domain.metrics.ProductRankingWeight.VIEW
import java.math.BigDecimal

object ProductRankingScoreCalculator {
    fun calculateScore(eventType: ProductRankingWeight, price: BigDecimal, amount: Int): BigDecimal =
        when (eventType) {
            VIEW -> eventType.weight * BigDecimal.ONE
            LIKE -> eventType.weight * BigDecimal.ONE
            SALE -> eventType.weight * price * BigDecimal(amount)
        }
}
