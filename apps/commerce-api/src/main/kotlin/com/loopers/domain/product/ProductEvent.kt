package com.loopers.domain.product

object ProductEvent {
    data class ProductViewed(val userId: String, val productId: Long)
}
