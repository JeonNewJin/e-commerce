package com.loopers.infrastructure.product

import com.loopers.domain.like.LikeTargetType.PRODUCT
import com.loopers.domain.like.QLikeCount.likeCount
import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductSortType
import com.loopers.domain.product.ProductSortType.LATEST
import com.loopers.domain.product.ProductSortType.LIKES_DESC
import com.loopers.domain.product.ProductSortType.PRICE_ASC
import com.loopers.domain.product.QProduct.product
import com.querydsl.core.types.Order.DESC
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Predicate
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository

@Repository
class ProductCustomRepository(private val query: JPAQueryFactory) {

    fun findProducts(command: ProductCommand.GetProducts): Page<Product> {
        val products = query
            .selectFrom(product)
            .where(command.brandId?.let { product.brandId.eq(it) })
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
            LIKES_DESC -> {
                val likeCountSubQuery = JPAExpressions
                    .select(likeCount.count.coalesce(0L))
                    .from(likeCount)
                    .where(
                        likeCount.target.type.eq(PRODUCT)
                            .and(likeCount.target.id.eq(product.id)) as Predicate?,
                    )
                OrderSpecifier(DESC, likeCountSubQuery)
            }

            else -> product.publishedAt.desc()
        }
}
