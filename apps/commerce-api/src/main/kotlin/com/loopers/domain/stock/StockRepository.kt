package com.loopers.domain.stock

interface StockRepository {

    fun find(productId: Long): Stock?

    fun findByProductId(productId: Long): Stock?

    fun save(stock: Stock)
}
