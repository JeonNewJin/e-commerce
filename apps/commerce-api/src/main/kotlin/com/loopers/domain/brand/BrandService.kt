package com.loopers.domain.brand

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class BrandService(private val brandRepository: BrandRepository) {

    fun getBrand(brandId: Long): BrandInfo =
        brandRepository.findById(brandId)
            ?.let { BrandInfo.from(it) }
            ?: throw CoreException(NOT_FOUND, "해당 브랜드를 찾을 수 없습니다.")

    fun getBrands(brandIds: List<Long>): List<BrandInfo> =
        brandRepository.findByIds(brandIds)
            .map { BrandInfo.from(it) }
}
