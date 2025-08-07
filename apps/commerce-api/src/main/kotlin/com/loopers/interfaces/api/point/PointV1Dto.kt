package com.loopers.interfaces.api.point

import com.loopers.application.point.PointWalletInput
import com.loopers.application.point.PointWalletOutput
import com.loopers.domain.point.vo.Point
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

class PointV1Dto private constructor() {

    class Request {

        data class Charge(
            @Schema(description = "충전 금액", example = "1000")
            @field:NotNull
            @field:DecimalMin(value = "1", message = "충전 금액은 0 보다 커야 합니다.")
            val amount: BigDecimal,
        ) {
            fun toInput(loginId: String) =
                PointWalletInput.Charge(
                    loginId = loginId,
                    amount = Point.of(amount),
                )
        }
    }

    class Response {

        data class PointResponse(val balance: BigDecimal) {
            companion object {
                fun from(output: PointWalletOutput): PointResponse =
                    PointResponse(
                        balance = output.balance,
                    )
            }
        }
    }
}
