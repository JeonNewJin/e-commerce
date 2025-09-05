package com.loopers.infrastructure.support.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.config.redis.RedisConfig
import com.loopers.domain.support.cache.CacheTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class RedisCacheTemplate(
    @param:Qualifier(RedisConfig.REDIS_TEMPLATE_MASTER)
    private val redisMaster: RedisTemplate<String, String>,
    private val redis: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : CacheTemplate {

    override fun <T> set(key: String, value: T) {
        val valueAsString = objectMapper.writeValueAsString(value)
        redisMaster.opsForValue().set(key, valueAsString)
    }

    override fun <T> set(key: String, value: T, ttl: Duration) {
        val valueAsString = objectMapper.writeValueAsString(value)
        redisMaster.opsForValue().set(key, valueAsString, ttl.toMillis(), TimeUnit.MILLISECONDS)
    }

    override fun <T> get(key: String, type: TypeReference<T>): T? {
        val valueAsString = redis.opsForValue().get(key) ?: return null
        return objectMapper.readValue(valueAsString, type)
    }

    override fun evict(key: String) {
        redisMaster.delete(key)
    }

    override fun getTimeToLive(key: String): Long? = redis.getExpire(key, TimeUnit.SECONDS)
}
