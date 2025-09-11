package com.loopers.domain.event

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class EventHandledService(private val eventHandledRepository: EventHandledRepository) {

    fun findAllByEventIds(eventIds: List<String>): List<EventHandledInfo> =
        eventHandledRepository.findAllByEventIds(eventIds)
            .map { EventHandledInfo.from(it) }

    @Transactional
    fun saveAll(command: List<EventHandledCommand.Create>) {
        val eventHandles = command.map {
            EventHandled(
                eventId = it.eventId,
                topic = it.topic,
                partition = it.partition,
                offset = it.offset,
                timestamp = it.timestamp,
            )
        }
        eventHandledRepository.saveAll(eventHandles)
    }
}
