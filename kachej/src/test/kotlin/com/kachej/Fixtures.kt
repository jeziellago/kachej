package com.kachej

import java.io.Serializable


data class MyValue(val value: Any) : Serializable

data class MyObject(
    val a: Int,
    val b: Float,
    val c: Double,
    val d: Boolean,
    val e: String,
    val f: List<MyValue>,
    val g: Set<MyValue>,
    val h: Map<Any, Any>
) : Serializable

val serializableObject = MyObject(
    1,
    1F,
    1.0,
    true,
    "value",
    listOf(MyValue("v1"), MyValue(2), MyValue(MyValue(0))),
    setOf(MyValue("set")),
    mapOf("key" to "value")
)

val unserializableObject = MyObject(
    1,
    1F,
    1.0,
    true,
    "value",
    listOf(MyValue(Any())),
    setOf(MyValue("set")),
    mapOf("key" to Any())
)