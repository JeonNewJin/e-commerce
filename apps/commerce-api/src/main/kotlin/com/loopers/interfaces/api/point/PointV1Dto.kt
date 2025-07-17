package com.loopers.interfaces.api.point

import com.loopers.application.point.PointInfo
import com.loopers.domain.point.PointCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

object PointV1Dto {

    class Request {

        data class Charge(
            @Schema(description = "충전 금액", example = "1000")
            @field:NotNull
            @field:DecimalMin(value = "1", message = "충전 금액은 0 보다 커야 합니다.")
            val amount: BigDecimal,
        ) {
            fun toCommand(userId: String) = PointCommand.Charge(
                userId = userId,
                amount = amount,
            )
        }
    }

    class Response {

        data class PointResponse(val balance: BigDecimal) {
            companion object {
                fun from(pointInfo: PointInfo): PointResponse =
                    PointResponse(
                        balance = pointInfo.balance,
                    )
            }
        }
    }
}
