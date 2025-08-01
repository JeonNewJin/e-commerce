package com.loopers.infrastructure.brand

import com.loopers.domain.brand.Brand
import com.loopers.domain.brand.BrandRepository
import org.springframework.stereotype.Component

@Component
class BrandCoreRepository(private val brandJpaRepository: BrandJpaRepository) : BrandRepository {

    override fun findById(brandId: Long): Brand? = brandJpaRepository.findById(brandId).orElse(null)

    override fun findByIds(brandIds: List<Long>): List<Brand> {
        if (brandIds.isEmpty()) {
            return emptyList()
        }

        return brandJpaRepository.findByIdIn(brandIds)
    }
}
