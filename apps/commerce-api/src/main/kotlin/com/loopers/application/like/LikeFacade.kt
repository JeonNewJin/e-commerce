package com.loopers.application.like

import com.loopers.domain.brand.BrandService
import com.loopers.domain.like.LikeService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.vo.LoginId
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class LikeFacade(
    private val userService: UserService,
    private val likeService: LikeService,
    private val productService: ProductService,
    private val brandService: BrandService,
) {

    @Transactional
    fun like(input: LikeInput.Like) {
        val user = userService.getUser(LoginId(input.loginId))

        likeService.like(input.toCommand(user.id))
    }

    @Transactional
    fun unlike(input: LikeInput.Unlike) {
        val user = userService.getUser(LoginId(input.loginId))

        likeService.unlike(input.toCommand(user.id))
    }

    @Transactional(readOnly = true)
    fun getLikedProducts(input: LikeInput.FindLikes): LikedProductsOutput {
        val user = userService.getUser(LoginId(input.loginId))
        val likes = likeService.findLikes(input.toCommand(user.id))
        val products = productService.findProductsByIds(likes.content.map { it.targetId })
        val brands = brandService.getBrands(products.map { it.brandId }.distinct())
        return LikedProductsOutput.of(likes, products, brands)
    }
}
