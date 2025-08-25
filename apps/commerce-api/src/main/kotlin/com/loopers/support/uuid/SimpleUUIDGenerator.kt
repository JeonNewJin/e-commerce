package com.loopers.support.uuid

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SimpleUUIDGenerator : UUIDGenerator {

    override fun generate(): String = UUID.randomUUID().toString()
}
