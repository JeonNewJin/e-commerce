package com.loopers.domain.stock

import com.loopers.domain.stock.entity.Stock

interface StockRepository {

    fun find(productId: Long): Stock?

    fun findByProductId(productId: Long): Stock?

    fun save(stock: Stock)
}
