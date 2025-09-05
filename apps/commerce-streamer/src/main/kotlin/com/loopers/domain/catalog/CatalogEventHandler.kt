package com.loopers.domain.catalog

interface CatalogEventHandler {

    fun eventHandleMethod(): EventHandleMethod

    fun process(command: CatalogCommand.Handle)
}
