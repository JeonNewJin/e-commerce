package com.loopers.domain.catalog

object CatalogCommand {
    data class Handle(
        val eventId: String,
        val eventType: EventHandleMethod,
        val productId: Long,
        val userId: Long? = null,
        val quantity: Int? = 0,
    )
}
