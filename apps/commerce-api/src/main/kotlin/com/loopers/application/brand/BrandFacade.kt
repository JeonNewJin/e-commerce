package com.loopers.application.brand

import com.loopers.domain.brand.BrandService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BrandFacade(private val brandService: BrandService) {

    @Transactional(readOnly = true)
    fun getBrand(brandId: Long): BrandOutput =
        brandService.getBrand(brandId)
            .let { BrandOutput.from(it) }
}
