package com.loopers.domain.event

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.apache.kafka.shaded.com.google.protobuf.Timestamp

@Entity
@Table(name = "event_handled")
class EventHandled(eventId: String, topic: String, partition: Int, offset: Long, timestamp: Timestamp) : BaseEntity() {

    val eventId: String = eventId
    val topic: String = topic
    val partition: Int = partition
    val offset: Long = offset
    val timestamp: Timestamp = timestamp
}
