package com.loopers.application.product

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.model.ProductSortType
import org.springframework.data.domain.Pageable

class ProductInput private constructor() {

    data class FindProductsOnSale(val brandId: Long? = null, val sortType: ProductSortType? = null, val pageable: Pageable) {
        fun toCommand(): ProductCommand.FindProductsOnSale =
            ProductCommand.FindProductsOnSale(
                brandId = brandId,
                sortType = sortType,
                pageable = pageable,
            )
    }
}
