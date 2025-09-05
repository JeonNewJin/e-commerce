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

class LikeCreatedEventListenerTest {

    private val productService = mockk<ProductService>()
    private val likeEventProducer = mockk<LikeEventProducer>()
    private lateinit var listener: LikeCreatedEventListener

    @BeforeEach
    fun setUp() {
        listener = LikeCreatedEventListener(productService, likeEventProducer)
        every { productService.increaseLikeCount(any()) } returns Unit
    }

    @Test
    fun `LikeCreated 이벤트 발생 시, 상품 좋아요 카운트 증가가 호출된다`() {
        // given
        val event = LikeEvent.LikeCreated(
            userId = 1L,
            targetId = 100L,
            targetType = PRODUCT,
        )

        // when
        listener.handle(event)

        // then
        verify { productService.increaseLikeCount(event.targetId) }
    }
}
