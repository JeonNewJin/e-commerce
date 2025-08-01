package com.loopers.domain.brand

interface BrandRepository {

    fun findById(brandId: Long): Brand?

    fun findByIds(brandIds: List<Long>): List<Brand>
}
