package com.loopers.domain.event

import org.apache.kafka.shaded.com.google.protobuf.Timestamp

object EventHandledCommand {
    data class Create(val eventId: String, val topic: String, val partition: Int, val offset: Long, val timestamp: Timestamp)
}
