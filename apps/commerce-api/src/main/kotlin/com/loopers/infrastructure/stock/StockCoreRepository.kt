package com.loopers.infrastructure.stock

import com.loopers.domain.stock.entity.Stock
import com.loopers.domain.stock.StockRepository
import org.springframework.stereotype.Component

@Component
class StockCoreRepository(private val stockJpaRepository: StockJpaRepository) : StockRepository {

    override fun find(productId: Long): Stock? = stockJpaRepository.findByProductId(productId)

    override fun findByProductId(productId: Long): Stock? = stockJpaRepository.findByProductId(productId)

    override fun save(stock: Stock) {
        stockJpaRepository.save(stock)
    }

    override fun findByProductIdWithLock(productId: Long): Stock? =
        stockJpaRepository.findByProductIdWithLock(productId)
}
