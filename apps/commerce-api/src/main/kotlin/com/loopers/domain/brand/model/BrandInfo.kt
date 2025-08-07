package com.loopers.domain.brand.model

import com.loopers.domain.brand.entity.Brand

data class BrandInfo(val id: Long, val name: String, val description: String) {
    companion object {
        fun from(brand: Brand): BrandInfo =
            BrandInfo(
                id = brand.id,
                name = brand.name,
                description = brand.description,
            )
    }
}
