package com.loopers.domain.product

import org.springframework.data.domain.Pageable

class ProductCommand private constructor() {

    data class GetProducts(val brandId: Long? = null, val sortType: ProductSortType? = null, val pageable: Pageable)
}
