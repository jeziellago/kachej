package com.kachej

val serializableObject = KachejTest.MyObject(
    1,
    1F,
    1.0,
    true,
    "value",
    listOf(KachejTest.MyValue("v1"), KachejTest.MyValue(2), KachejTest.MyValue(KachejTest.MyValue(0))),
    setOf(KachejTest.MyValue("set")),
    mapOf("key" to "value")
)

val unserializableObject = KachejTest.MyObject(
    1,
    1F,
    1.0,
    true,
    "value",
    listOf(KachejTest.MyValue(Any())),
    setOf(KachejTest.MyValue("set")),
    mapOf("key" to Any())
)