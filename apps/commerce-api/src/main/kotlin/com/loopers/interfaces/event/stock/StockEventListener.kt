package com.loopers.interfaces.event.stock

import com.loopers.domain.stock.StockEvent
import com.loopers.domain.stock.StockEventProducer
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StockEventListener(private val stockEventProducer: StockEventProducer) {

    @EventListener
    fun handle(event: StockEvent.Deducted) {
        stockEventProducer.send(event)
    }
}
