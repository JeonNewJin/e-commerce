package com.loopers.domain.event

data class EventHandledInfo(val eventId: String) {
    companion object {
        fun from(eventHandled: EventHandled): EventHandledInfo =
            EventHandledInfo(
                eventId = eventHandled.eventId,
            )
    }
}
