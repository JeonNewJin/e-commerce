package com.loopers.interfaces.api.point

import com.loopers.application.point.PointInfo
import java.math.BigDecimal

object PointV1Dto {

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
