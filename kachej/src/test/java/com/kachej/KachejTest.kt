/*
 * Copyright 2020 Jeziel Lago.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kachej

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.NotSerializableException
import java.io.Serializable

@RunWith(JUnit4::class)
class KachejTest {

    private val serializableObject = MyObject(
        1,
        1F,
        1.0,
        true,
        "value",
        listOf(MyValue("v1"), MyValue(2), MyValue(MyValue(0))),
        setOf(MyValue("set")),
        mapOf("key" to "value")
    )

    private val unserializableObject = MyObject(
        1,
        1F,
        1.0,
        true,
        "value",
        listOf(MyValue(Any())),
        setOf(MyValue("set")),
        mapOf("key" to Any())
    )


    @Test
    fun `writer should write serializable object with success`() = runBlocking {
        val kachej = Kachej()

        assert(kachej.write("myObject", serializableObject))
    }

    @Test(expected = NotSerializableException::class)
    fun `writer should not write unserializable`() = runBlocking {
        val kachej = Kachej()

        assert(kachej.write("myObject", unserializableObject))
    }

    @Test
    fun `reader should read object from file`() = runBlocking {
        val kachej = Kachej()

        kachej.write("myObject", serializableObject)

        assertEquals(serializableObject, kachej.read<MyObject>("myObject"))
    }

    @After
    fun tearDown() {
        File("myObject").delete()
    }

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
}