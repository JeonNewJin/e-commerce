package com.loopers.domain.stock

object StockEvent {

    data class Deducted(val productId: Long, val quantity: Int)
}
