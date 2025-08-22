package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentMethod.CARD
import com.loopers.domain.payment.model.CardType
import com.loopers.infrastructure.payment.feign.PgClient
import com.loopers.support.IntegrationTestSupport
import com.ninjasquad.springmockk.MockkBean
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

class PgSimulatorCircuitBreakerTest(
    private val pgSimulator: PgSimulator,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    @MockkBean
    private val pgClient: PgClient,
) : IntegrationTestSupport() {

    @BeforeEach
    fun setUp() {
        circuitBreakerRegistry.circuitBreaker("pgCircuit").reset()
    }

    @Test
    fun `7회 중 3회 성공 4회 실패 시 서킷브레이커가 OPEN 상태가 된다`() {
        // given
        every { pgClient.requestPayment(any(), any()) }
            .returnsMany(
                listOf(
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                ),
            )
            .andThenAnswer { throw RuntimeException("PG 장애 #1") }
            .andThenAnswer { throw RuntimeException("PG 장애 #2") }
            .andThenAnswer { throw RuntimeException("PG 장애 #3") }
            .andThenAnswer { throw RuntimeException("PG 장애 #4") }

        val command = createCommand()

        // when
        repeat(7) {
            try {
                pgSimulator.requestPayment("user", command)
            } catch (_: Exception) {}
        }

        // then
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("pgCircuit")
        assertEquals("OPEN", circuitBreaker.state.name)
    }

    @Test
    fun `6회 중 3회 성공 3회 실패 시 서킷브레이커는 OPEN 상태가 아니다`() {
        // given
        every { pgClient.requestPayment(any(), any()) }
            .returnsMany(
                listOf(
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                ),
            )
            .andThenAnswer { throw RuntimeException("PG 장애 #1") }
            .andThenAnswer { throw RuntimeException("PG 장애 #2") }
            .andThenAnswer { throw RuntimeException("PG 장애 #3") }

        val command = createCommand()

        // when
        repeat(6) {
            try {
                pgSimulator.requestPayment("user", command)
            } catch (_: Exception) {}
        }

        // then
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("pgCircuit")
        assertEquals("CLOSED", circuitBreaker.state.name)
    }

    @Test
    fun `7회 중 3회 성공 4회 실패 후 대기 시간 경과 시 서킷브레이커가 HALF_OPEN 상태로 전환된다`() {
        // given
        every { pgClient.requestPayment(any(), any()) }
            .returnsMany(
                listOf(
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                ),
            )
            .andThenAnswer { throw RuntimeException("PG 장애 #1") }
            .andThenAnswer { throw RuntimeException("PG 장애 #2") }
            .andThenAnswer { throw RuntimeException("PG 장애 #3") }
            .andThenAnswer { throw RuntimeException("PG 장애 #4") }

        val command = createCommand()

        // when
        repeat(7) {
            try {
                pgSimulator.requestPayment("user", command)
            } catch (_: Exception) {}
        }

        Thread.sleep(2001)

        // then
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("pgCircuit")

        val seenState = AtomicReference<CircuitBreaker.State>()
        circuitBreaker.decorateSupplier {
            seenState.set(circuitBreaker.state)
        }.get()

        assertEquals("HALF_OPEN", seenState.get().name)
    }

    @Test
    fun `서킷브레이커가 OPEN 상태에서 HALF_OPEN을 거쳐 정상 호출 시 CLOSED로 복구된다`() {
        // given
        every { pgClient.requestPayment(any(), any()) }
            .returnsMany(
                listOf(
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                ),
            )
            .andThenAnswer { throw RuntimeException("PG 장애 #1") }
            .andThenAnswer { throw RuntimeException("PG 장애 #2") }
            .andThenAnswer { throw RuntimeException("PG 장애 #3") }
            .andThenAnswer { throw RuntimeException("PG 장애 #4") }

        val command = createCommand()

        repeat(7) {
            try {
                pgSimulator.requestPayment("user", command)
            } catch (_: Exception) {}
        }

        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("pgCircuit")
        assertEquals("OPEN", circuitBreaker.state.name)

        // when
        Thread.sleep(2001)
        every { pgClient.requestPayment(any(), any()) } returns mockk(relaxed = true)
        repeat(2) {
            try {
                pgSimulator.requestPayment("user", command)
            } catch (_: Exception) {}
        }

        // then
        assertEquals("CLOSED", circuitBreaker.state.name)
    }

    @Test
    fun `정상 호출이 반복되어도 서킷브레이커는 항상 CLOSED 상태이다`() {
        // given
        every { pgClient.requestPayment(any(), any()) } returns mockk(relaxed = true)
        val command = createCommand()

        // when
        repeat(20) {
            pgSimulator.requestPayment("user", command)

            // then
            val circuitBreaker = circuitBreakerRegistry.circuitBreaker("pgCircuit")
            assertEquals("CLOSED", circuitBreaker.state.name)
        }
    }

    @Test
    fun `7회 중 3회 성공 4회 슬로우콜 시 서킷브레이커가 OPEN 상태가 된다`() {
        // given
        every { pgClient.requestPayment(any(), any()) }
            .returnsMany(
                listOf(
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                ),
            )
            .andThenAnswer {
                Thread.sleep(3000)
                mockk(relaxed = true)
            }
            .andThenAnswer {
                Thread.sleep(3000)
                mockk(relaxed = true)
            }
            .andThenAnswer {
                Thread.sleep(3000)
                mockk(relaxed = true)
            }
            .andThenAnswer {
                Thread.sleep(3000)
                mockk(relaxed = true)
            }

        val command = createCommand()

        // when
        repeat(7) {
            try {
                pgSimulator.requestPayment("user", command)
            } catch (_: Exception) {}
        }

        // then
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("pgCircuit")
        assertEquals("OPEN", circuitBreaker.state.name)
    }

    private fun createCommand() =
        PaymentCommand.Pay(
        userId = 1L,
        orderCode = UUID.randomUUID().toString(),
        amount = BigDecimal.ONE,
        paymentMethod = CARD,
        cardType = CardType.SAMSUNG,
        cardNo = "1234-5678-9012-3456",
    )
}
