package com.loopers.domain.support.cache

import com.fasterxml.jackson.core.type.TypeReference

object CacheKeyGenerator {

    private const val DEFAULT_VERSION = "1.0"
    private const val VERSION_PREFIX = "v="
    private const val SEPARATOR = ":"

    fun generate(namespace: String, params: Map<String, Any?>, version: String? = DEFAULT_VERSION): String =
        buildString {
            append(namespace)

            if (!version.isNullOrBlank()) {
                append(SEPARATOR).append(VERSION_PREFIX).append(version)
            }

            params.forEach { (key, value) ->
                append(SEPARATOR).append(key).append("=").append(formatValue(value))
            }
        }

    fun formatValue(value: Any?): String = when (value) {
        null -> "null"
        is String -> value.ifBlank { "empty" }
        is Collection<*> -> if (value.isEmpty()) "empty" else value.joinToString(",") { formatValue(it) }
        is Array<*> -> if (value.isEmpty()) "empty" else value.joinToString(",") { formatValue(it) }
        else -> value.toString()
    }

    fun forProductsOnSale(sortType: String?, page: Int, brandId: Any? = "ALL"): String =
        generate(
            namespace = CacheKeys.PRODUCTS_ON_SALE,
            params = mapOf(
                "brandId" to brandId,
                "sort" to sortType,
                "page" to page,
            ),
        )

    inline fun <reified T> typeRef() = object : TypeReference<T>() {}
}
