package com.loopers.interfaces.event.like

import com.loopers.domain.like.LikeEvent
import com.loopers.domain.like.LikeEventProducer
import com.loopers.domain.product.ProductService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionalEventListener

@Component
class LikeDeletedEventListener(private val productService: ProductService, private val likeEventProducer: LikeEventProducer) {

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handle(event: LikeEvent.LikeDeleted) {
        productService.decreaseLikeCount(event.targetId)
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handleAuditLog(event: LikeEvent.LikeDeleted) {
        likeEventProducer.publish(event)
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handleMetrics(event: LikeEvent.LikeDeleted) {
        likeEventProducer.publish(event)
    }
}
