package com.loopers.application.product

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductSortType
import org.springframework.data.domain.Pageable

class ProductInput private constructor() {

    data class GetProducts(val brandId: Long? = null, val sortType: ProductSortType? = null, val pageable: Pageable) {
        fun toCommand(): ProductCommand.GetProducts =
            ProductCommand.GetProducts(
                brandId = brandId,
                sortType = sortType,
                pageable = pageable,
            )
    }
}
