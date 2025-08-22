package com.loopers.interfaces.api.order

import com.loopers.application.order.OrderFacade
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderV1Controller(private val orderFacade: OrderFacade) {

    @PostMapping
    fun placeOrder(
        @Valid @RequestBody request: OrderV1Dto.Request.Order,
        @RequestHeader("X-USER-ID") userId: String,
    ): ApiResponse<Unit> {
        orderFacade.placeOrder(request.toInput(userId))
        return ApiResponse.success(Unit)
    }
}
