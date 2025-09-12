package com.loopers.infrastructure.metrics

import com.loopers.domain.metrics.ProductRankingRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class ProductRankingCoreRepository(private val redisTemplate: RedisTemplate<String, String>) : ProductRankingRepository {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    override fun accumulateScore(productId: Long, score: BigDecimal, date: LocalDate) {
        val key = "ranking:all:${date.format(dateFormatter)}"
        val member = productId.toString()
        val scoreValue = score.toDouble()
        redisTemplate.opsForZSet().add(key, member, scoreValue)
    }
}
