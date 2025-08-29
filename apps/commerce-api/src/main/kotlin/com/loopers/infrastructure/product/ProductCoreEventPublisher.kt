package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEvent
import com.loopers.domain.product.ProductEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ProductCoreEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) : ProductEventPublisher {

    override fun publish(event: ProductEvent.ProductViewed) {
        applicationEventPublisher.publishEvent(event)
    }
}
