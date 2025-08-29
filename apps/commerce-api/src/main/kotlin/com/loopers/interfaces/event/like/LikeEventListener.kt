package com.loopers.interfaces.event.like

import com.loopers.domain.like.LikeEvent
import com.loopers.domain.product.ProductService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionalEventListener

@Component
class LikeEventListener(private val productService: ProductService) {

    private val logger = LoggerFactory.getLogger(LikeEventListener::class.java)

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handle(event: LikeEvent.LikeCreated) {
        logger.info("Like Created Event : {}", event)

        productService.increaseLikeCount(event.targetId)
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handle(event: LikeEvent.LikeDeleted) {
        logger.info("Like Deleted Event : {}", event)

        productService.decreaseLikeCount(event.targetId)
    }
}
