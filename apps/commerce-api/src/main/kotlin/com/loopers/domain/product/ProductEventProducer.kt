package com.loopers.domain.product

interface ProductEventProducer {
    fun send(event: ProductEvent.ProductViewed)
}
