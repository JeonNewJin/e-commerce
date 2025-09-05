package com.loopers.domain.catalog.handler

import com.loopers.domain.catalog.CatalogCommand
import com.loopers.domain.catalog.CatalogEventHandler
import com.loopers.domain.catalog.EventHandleMethod
import com.loopers.domain.catalog.EventHandleMethod.STOCK_ADJUSTED
import com.loopers.domain.support.cache.CacheKeyGenerator
import com.loopers.domain.support.cache.CacheKeys
import com.loopers.domain.support.cache.CacheTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StockAdjustedHandler(private val cacheTemplate: CacheTemplate) : CatalogEventHandler {

    override fun eventHandleMethod(): EventHandleMethod = STOCK_ADJUSTED

    @Transactional
    override fun process(command: CatalogCommand.Handle) {
        val cacheKey = CacheKeyGenerator.generate(
            namespace = CacheKeys.PRODUCT_DETAIL,
            params = mapOf("productId" to command.productId),
        )
        cacheTemplate.evict(cacheKey)
    }
}
