package com.loopers.domain.support.cache

import com.fasterxml.jackson.core.type.TypeReference
import java.time.Duration

interface CacheTemplate {

    fun <T> set(key: String, value: T)

    fun <T> set(key: String, value: T, ttl: Duration)

    fun <T> get(key: String, type: TypeReference<T>): T?

    fun evict(key: String)

    fun getTimeToLive(key: String): Long?
}
