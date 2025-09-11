package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockEvent
import com.loopers.domain.stock.StockEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class StockCoreEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) : StockEventPublisher {

    override fun publish(event: StockEvent.Deducted) {
        applicationEventPublisher.publishEvent(event)
    }
}
