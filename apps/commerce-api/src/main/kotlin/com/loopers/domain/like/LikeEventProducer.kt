package com.loopers.domain.like

interface LikeEventProducer {
    fun publish(event: LikeEvent.LikeCreated)
    fun publish(event: LikeEvent.LikeDeleted)
}
