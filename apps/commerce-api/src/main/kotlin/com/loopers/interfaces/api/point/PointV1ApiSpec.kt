package com.loopers.interfaces.api.point

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Point V1 API", description = "포인트 API 입니다.")
interface PointV1ApiSpec {

    @Operation(
        summary = "포인트 정보 조회",
        description = "포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환합니다.",
    )
    fun getPoint(
        @Parameter(
            name = "X-USER-ID",
            description = "회원가입 때 가입한 ID 입니다.",
            required = true,
            `in` = ParameterIn.HEADER,
        )
        userId: String,
    ): ApiResponse<PointV1Dto.Response.PointResponse>

    @Operation(
        summary = "포인트 충전",
        description = "포인트를 충전에 성공할 경우, 충전된 보유 총량을 응답으로 반환합니다.",
    )
    fun chargePoint(
        @Parameter(
            name = "X-USER-ID",
            description = "회원가입 때 가입한 ID 입니다.",
            required = true,
            `in` = ParameterIn.HEADER,
        )
        userId: String,

        @RequestBody(
            description = "포인트 충전 파라미터",
            required = true,
            content = [
                Content(
                    schema = Schema(PointV1Dto.Request.Charge::class),
                ),
            ],
        )
        request: PointV1Dto.Request.Charge,
    ): ApiResponse<PointV1Dto.Response.PointResponse>
}
