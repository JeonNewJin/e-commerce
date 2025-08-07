package com.loopers.interfaces.api.brand

import com.loopers.domain.brand.model.BrandInfo

class BrandV1Dto private constructor() {

    class Response {

        data class BrandResponse(val id: Long, val name: String, val description: String) {
            companion object {
                fun from(output: BrandInfo): BrandResponse =
                    BrandResponse(
                        id = output.id,
                        name = output.name,
                        description = output.description,
                    )
            }
        }
    }
}
