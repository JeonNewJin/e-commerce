package com.loopers.interfaces.event.product

import com.loopers.domain.product.ProductEvent
import com.loopers.domain.product.ProductEventProducer
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ProductEventListener(private val productEventProducer: ProductEventProducer) {

    @EventListener
    fun handle(event: ProductEvent.ProductViewed) {
        productEventProducer.send(event)
    }
}
