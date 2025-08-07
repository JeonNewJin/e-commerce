package com.loopers.domain.brand

import com.loopers.domain.brand.entity.Brand

interface BrandRepository {

    fun findById(brandId: Long): Brand?

    fun findByIds(brandIds: List<Long>): List<Brand>
}
