package com.loopers.domain.stock

interface StockEventPublisher {
    fun publish(event: StockEvent.Deducted)
}
