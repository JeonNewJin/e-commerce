package com.loopers.interfaces.event.like

import com.loopers.domain.like.LikeEvent
import com.loopers.domain.like.LikeEventProducer
import com.loopers.domain.like.model.LikeableType.PRODUCT
import com.loopers.domain.product.ProductService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LikeDeletedEventListenerTest {

    private val productService = mockk<ProductService>()
    private val likeEventProducer = mockk<LikeEventProducer>()
    private lateinit var listener: LikeDeletedEventListener

    @BeforeEach
    fun setUp() {
        listener = LikeDeletedEventListener(productService, likeEventProducer)
        every { productService.decreaseLikeCount(any()) } returns Unit
    }

    @Test
    fun `LikeDeleted 이벤트 발생 시, 상품 좋아요 카운트 감소가 호출된다`() {
        // given
        val event = LikeEvent.LikeDeleted(
            userId = 1L,
            targetId = 100L,
            targetType = PRODUCT,
        )

        // when
        listener.handle(event)

        // then
        verify { productService.decreaseLikeCount(event.targetId) }
    }
}
