package com.loopers.infrastructure.external

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DataPlatformMockApiClient {

    private val logger = LoggerFactory.getLogger(DataPlatformMockApiClient::class.java)

    fun sendData(data: String) {
        logger.info("Mock sending data to Data Platform: $data")
    }
}
