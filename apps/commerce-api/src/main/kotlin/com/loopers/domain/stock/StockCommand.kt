package com.loopers.domain.stock

class StockCommand private constructor() {

    data class Deduct(val productId: Long, val quantity: Int)
}
