package com.loopers.domain.catalog

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CatalogService(catalogEventHandlers: List<CatalogEventHandler>) {

    private val catalogEventHandlerMap by lazy { catalogEventHandlers.associateBy { it.eventHandleMethod() } }

    @Transactional
    fun handle(command: CatalogCommand.Handle) {
        val handler = catalogEventHandlerMap[command.eventType]
            ?: throw IllegalStateException("지원하지 않는 이벤트 타입입니다: ${command.eventType}")

        handler.process(command)
    }
}
