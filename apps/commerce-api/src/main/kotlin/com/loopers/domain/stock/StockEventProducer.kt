package com.loopers.domain.stock

interface StockEventProducer {
    fun send(event: StockEvent.Deducted)
}
