package com.loopers.domain.product

import com.loopers.domain.product.model.ProductSortType
import org.springframework.data.domain.Pageable

class ProductCommand private constructor() {

    data class FindProductsOnSale(val brandId: Long? = null, val sortType: ProductSortType? = null, val pageable: Pageable)
}
