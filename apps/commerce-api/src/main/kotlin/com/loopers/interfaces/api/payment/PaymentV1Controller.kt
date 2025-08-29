package com.loopers.interfaces.api.payment

import com.loopers.domain.payment.PaymentService
import com.loopers.interfaces.api.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/payments")
class PaymentV1Controller(private val paymentService: PaymentService) {

    @PostMapping("/callback")
    fun callback(
        @RequestBody request: PaymentV1Dto.Request.Callback,
    ): ApiResponse<Unit> {
        val paidAt = LocalDateTime.now().toString()
        paymentService.complete(request.toCommand(paidAt))
        return ApiResponse.success(Unit)
    }
}
