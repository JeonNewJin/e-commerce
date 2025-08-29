package com.loopers.application.order

import com.loopers.domain.brand.entity.Brand
import com.loopers.domain.order.OrderEvent
import com.loopers.domain.payment.PaymentMethod.POINT
import com.loopers.domain.point.entity.PointWallet
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.entity.Product
import com.loopers.domain.product.model.ProductStatus.SALE
import com.loopers.domain.stock.entity.Stock
import com.loopers.domain.user.entity.User
import com.loopers.domain.user.model.Gender.MALE
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.order.OrderJpaRepository
import com.loopers.infrastructure.point.PointWalletJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.stock.StockJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.event.ApplicationEvents
import java.math.BigDecimal
import java.time.Duration
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

    @Autowired
    lateinit var applicationEvents: ApplicationEvents

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
            balance = Point.of(50_000L),
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
                OrderInput.Order.OrderItem(
                    productId = product.id,
                    quantity = 1,
                ),
            ),
            couponId = null,
            paymentMethod = POINT,
            cardType = null,
            cardNo = null,
        )

        // When
        orderFacade.placeOrder(input)

        // Then
        val orders = orderJpaRepository.findAll()

        assertAll(
            { assertThat(orders).hasSize(1) },
            { assertThat(orders[0].userId).isEqualTo(user.id) },
            { assertThat(orders[0].totalPrice).isEqualTo(BigDecimal("10000.00")) },
        )

        await()
            .atMost(Duration.ofSeconds(2))
            .pollInterval(Duration.ofMillis(100))
            .untilAsserted {
                val updatedPointWallet = pointWalletJpaRepository.findByUserId(user.id)!!
                assertThat(updatedPointWallet.balance.value).isEqualTo(BigDecimal("40000.00"))
            }
    }

    @Test
    fun `주문 요청 시, 주문이 성공적으로 생성되면, 주문 완료 이벤트가 발행된다`() {
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
            balance = Point.of(50_000L),
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
                OrderInput.Order.OrderItem(
                    productId = product.id,
                    quantity = 1,
                ),
            ),
            couponId = null,
            paymentMethod = POINT,
            cardType = null,
            cardNo = null,
        )

        // When
        val actual = orderFacade.placeOrder(input)

        // Then
        val eventCount = applicationEvents.stream(OrderEvent.OrderPlaced::class.java)
            .filter { it.orderCode == actual.orderCode }
            .count()

        assertThat(eventCount).isEqualTo(1)
    }
}
