package com.loopers.application.brand

import com.loopers.domain.brand.BrandInfo

data class BrandOutput(val id: Long, val name: String, val description: String) {
    companion object {
        fun from(info: BrandInfo): BrandOutput =
            BrandOutput(
                id = info.id,
                name = info.name,
                description = info.description,
            )
    }
}
