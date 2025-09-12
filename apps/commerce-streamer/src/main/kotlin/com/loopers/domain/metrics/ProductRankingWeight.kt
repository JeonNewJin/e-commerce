package com.loopers.domain.metrics

import java.math.BigDecimal

enum class ProductRankingWeight(val weight: BigDecimal) {
    VIEW(BigDecimal("1.0")),
    LIKE(BigDecimal("5.0")),
    SALE(BigDecimal("10.0")),
}
