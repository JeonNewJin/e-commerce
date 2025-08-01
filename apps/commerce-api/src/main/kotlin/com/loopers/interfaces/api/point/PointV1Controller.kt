package com.loopers.interfaces.api.point

import com.loopers.application.point.PointWalletFacade
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/points")
class PointV1Controller(private val pointWalletFacade: PointWalletFacade) : PointV1ApiSpec {

    @GetMapping
    override fun getPoint(
        @RequestHeader("X-USER-ID", required = true) userId: kotlin.String,
    ): ApiResponse<PointV1Dto.Response.PointResponse> =
        pointWalletFacade.getPoint(userId)
            .let { PointV1Dto.Response.PointResponse.from(it) }
            .let { ApiResponse.success(it) }

    @PostMapping("/charge")
    override fun chargePoint(
        @RequestHeader("X-USER-ID", required = true) userId: kotlin.String,
        @Valid @RequestBody request: PointV1Dto.Request.Charge,
    ): ApiResponse<PointV1Dto.Response.PointResponse> =
        pointWalletFacade.charge(request.toInput(userId))
            .let { PointV1Dto.Response.PointResponse.from(it) }
            .let { ApiResponse.success(it) }
}
