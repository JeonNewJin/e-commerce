package com.loopers.application.order

import com.loopers.domain.brand.Brand
import com.loopers.domain.point.Point
import com.loopers.domain.point.PointWallet
import com.loopers.domain.product.Product
import com.loopers.domain.product.ProductStatus.SALE
import com.loopers.domain.stock.Stock
import com.loopers.domain.user.Gender.MALE
import com.loopers.domain.user.User
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.order.OrderJpaRepository
import com.loopers.infrastructure.point.PointWalletJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.stock.StockJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal
import java.time.LocalDateTime

class OrderFacadeIntegrationTest(
    private val orderFacade: OrderFacade,
    private val userJpaRepository: UserJpaRepository,
    private val pointWalletJpaRepository: PointWalletJpaRepository,
    private val brandJpaRepository: BrandJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val stockJpaRepository: StockJpaRepository,
    private val orderJpaRepository: OrderJpaRepository,
) : IntegrationTestSupport() {

    @Test
    fun `주문 요청 시, 주문이 성공적으로 생성된다`() {
        // Given
        val user = User(
            loginId = "wjsyuwls",
            email = "wjsyuwls@google.com",
            birthdate = "2000-01-01",
            gender = MALE,
        )
        userJpaRepository.save(user)

        val pointWallet = PointWallet(
            userId = user.id,
            balance = Point(50_000L),
        )
        pointWalletJpaRepository.save(pointWallet)

        val brand = Brand(
            name = "무신사",
            description = "브랜드 설명입니다.",
        )
        brandJpaRepository.save(brand)

        val product = Product(
            name = "테스트 상품",
            price = BigDecimal(10_000L),
            brandId = 1L,
            publishedAt = LocalDateTime.now().plusDays(1L).toString(),
            status = SALE,
        )
        productJpaRepository.save(product)

        val stock = Stock(
            productId = product.id,
            quantity = 100,
        )
        stockJpaRepository.save(stock)

        val input = OrderInput.Order(
            loginId = "wjsyuwls",
            orderItems = listOf(
                OrderInput.OrderItem(
                    productId = product.id,
                    quantity = 1,
                ),
            ),
        )

        // When
        orderFacade.placeOrder(input)

        // Then
        val updatedPointWallet = pointWalletJpaRepository.findByUserId(user.id)!!
        val updatedStock = stockJpaRepository.findByProductId(product.id)!!
        val orders = orderJpaRepository.findAll()

        assertAll(
            { assertThat(updatedPointWallet.balance.value).isEqualTo(BigDecimal("40000.00")) },
            { assertThat(updatedStock.quantity).isEqualTo(99) },
            { assertThat(orders).hasSize(1) },
            { assertThat(orders[0].userId).isEqualTo(user.id) },
            { assertThat(orders[0].totalPrice).isEqualTo(BigDecimal("10000.00")) },
        )
    }
}
