package com.loopers.domain.metrics

import java.math.BigDecimal
import java.time.LocalDate

interface ProductRankingRepository {

    fun accumulateScore(productId: Long, score: BigDecimal, date: LocalDate)
}
