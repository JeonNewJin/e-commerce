package com.loopers.interfaces.api.point

import com.loopers.application.point.PointFacade
import com.loopers.interfaces.api.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/points")
class PointV1Controller(private val pointFacade: PointFacade) : PointV1ApiSpec {

    @GetMapping
    override fun myPoints(
        @RequestHeader("X-USER-ID", required = true) userId: String,
    ): ApiResponse<PointV1Dto.Response.PointResponse> = pointFacade.myPoints(userId)
        .let { PointV1Dto.Response.PointResponse.from(it) }
        .let { ApiResponse.success(it) }
}
