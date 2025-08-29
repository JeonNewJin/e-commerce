package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeEvent
import com.loopers.domain.like.LikeEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class LikeCoreEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) : LikeEventPublisher {

    override fun publish(event: LikeEvent.LikeCreated) {
        applicationEventPublisher.publishEvent(event)
    }

    override fun publish(event: LikeEvent.LikeDeleted) {
        applicationEventPublisher.publishEvent(event)
    }
}
