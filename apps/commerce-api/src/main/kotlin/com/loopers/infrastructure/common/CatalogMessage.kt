package com.loopers.infrastructure.common

import com.loopers.domain.like.LikeEvent
import com.loopers.domain.product.ProductEvent
import com.loopers.domain.stock.StockEvent
import com.loopers.infrastructure.common.CatalogMessage.EventType.PRODUCT_LIKED
import com.loopers.infrastructure.common.CatalogMessage.EventType.PRODUCT_UNLIKED
import com.loopers.infrastructure.common.CatalogMessage.EventType.PRODUCT_VIEWED
import com.loopers.infrastructure.common.CatalogMessage.EventType.STOCK_ADJUSTED

data class CatalogMessage(
    val eventId: String,
    val eventType: EventType,
    val productId: Long,
    val userId: Long? = null,
    val quantity: Int? = 0,
) {
    companion object {
        fun from(event: LikeEvent.LikeCreated, eventId: String): CatalogMessage =
            CatalogMessage(
                eventId = eventId,
                eventType = PRODUCT_LIKED,
                productId = event.targetId,
                userId = event.userId,
            )

        fun from(event: LikeEvent.LikeDeleted, eventId: String): CatalogMessage =
            CatalogMessage(
                eventId = eventId,
                eventType = PRODUCT_UNLIKED,
                productId = event.targetId,
                userId = event.userId,
            )

        fun from(event: ProductEvent.ProductViewed, eventId: String): CatalogMessage =
            CatalogMessage(
                eventId = eventId,
                eventType = PRODUCT_VIEWED,
                productId = event.productId,
            )

        fun from(event: StockEvent.Deducted, eventId: String): CatalogMessage =
            CatalogMessage(
                eventId = eventId,
                eventType = STOCK_ADJUSTED,
                productId = event.productId,
                quantity = event.quantity,
            )
    }

    enum class EventType {
        PRODUCT_LIKED,
        PRODUCT_UNLIKED,
        PRODUCT_VIEWED,
        STOCK_ADJUSTED,
    }
}
