package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.entity.Product
import com.loopers.domain.product.entity.QProduct.product
import com.loopers.domain.product.model.ProductSortType
import com.loopers.domain.product.model.ProductSortType.LATEST
import com.loopers.domain.product.model.ProductSortType.LIKES_DESC
import com.loopers.domain.product.model.ProductSortType.PRICE_ASC
import com.loopers.domain.product.model.ProductStatus.SALE
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository

@Repository
class ProductCustomRepository(private val query: JPAQueryFactory) {

    fun findProductsOnSale(command: ProductCommand.FindProductsOnSale): Page<Product> {
        val products = query
            .selectFrom(product)
            .where(
                command.brandId?.let { product.brandId.eq(it) },
                product.status.eq(SALE),
            )
            .orderBy(getOrderBy(command.sortType))
            .offset(command.pageable.offset)
            .limit(command.pageable.pageSize.toLong())
            .fetch()

        val total = query
            .select(product.count())
            .from(product)
            .where(command.brandId?.let { product.brandId.eq(it) })
            .fetchOne() ?: 0L

        return PageImpl(products, command.pageable, total)
    }

    private fun getOrderBy(sortType: ProductSortType?): OrderSpecifier<*>? =
        when (sortType) {
            LATEST -> product.publishedAt.desc()
            PRICE_ASC -> product.price.asc()
            LIKES_DESC -> product.likeCount.desc()
            else -> product.publishedAt.desc()
        }
}
