package com.loopers.domain.event

interface EventHandledRepository {
    fun findAllByEventIds(eventIds: List<String>): List<EventHandled>
    fun saveAll(eventHandles: List<EventHandled>)
}
