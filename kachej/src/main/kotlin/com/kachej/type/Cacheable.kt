package com.kachej.type

import java.io.Serializable

fun <T: Serializable> cacheOf(vararg items: T): CacheableList<T> {
    return CacheableList(items.toList())
}

fun <V: Serializable> cacheOf(vararg items: Pair<String, V>): CacheableMap {
    return CacheableMap(items.toMap())
}

data class CacheableList<T: Serializable>(val items: Collection<T>) : Serializable

data class CacheableMap(val map: Map<String, Serializable>) : Serializable
