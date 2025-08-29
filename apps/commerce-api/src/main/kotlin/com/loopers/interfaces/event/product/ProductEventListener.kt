package com.loopers.interfaces.event.product

import com.loopers.domain.product.ProductEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ProductEventListener {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun handle(event: ProductEvent.ProductViewed) {
        logger.info("Product Viewed Event : {}", event)
    }
}
