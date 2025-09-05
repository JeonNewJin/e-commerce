package com.loopers.infrastructure.event

import com.loopers.domain.event.EventHandled
import com.loopers.domain.event.EventHandledRepository
import org.springframework.stereotype.Component

@Component
class EventHandledCoreRepository(private val eventHandledJpaRepository: EventHandledJpaRepository) : EventHandledRepository {

    override fun findAllByEventIds(eventIds: List<String>): List<EventHandled> {
        if (eventIds.isEmpty()) return emptyList()
        return eventHandledJpaRepository.findAllByEventIdIn(eventIds)
    }

    override fun saveAll(eventHandles: List<EventHandled>) {
        eventHandledJpaRepository.saveAll(eventHandles)
    }
}
