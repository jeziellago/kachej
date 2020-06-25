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

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        val kachej = getKachej()
        var success = false

        kachej.write(OBJECT_WRITE, serializableObject)
            .collect { success = true }

        assert(success)
    }

    @Test
    fun `writer should rewrite serializable object with success`() = runBlocking {
        val kachej = getKachej()
        var success = false
        kachej.write(OBJECT_WRITE, serializableObject).single()

        kachej.write(OBJECT_WRITE, serializableObject)
            .collect { success = true }

        assert(success)
    }

    @Test(expected = NotSerializableException::class)
    fun `writer should not write unserializable`() = runBlocking {
        val kachej = getKachej()

        kachej.write(OBJECT_WRITE, unserializableObject).single()
    }

    @Test
    fun `reader should read object from file`() = runBlocking {
        val kachej = getKachej()

        kachej.write(OBJECT_READ, serializableObject).single()

        assertEquals(serializableObject, kachej.read<MyObject>(OBJECT_READ).single())
    }

    @Test
    fun `clean file should clean object from cache`() = runBlocking {
        var fileCleaned = false
        with(File("/tmp/kachej", OBJECT_CLEAN_SINGLE)) {
            parentFile.mkdirs()
            writeText("text")
            assertTrue(exists())
        }

        getKachej().clean(OBJECT_CLEAN_SINGLE).collect { fileCleaned = true }

        assertTrue(fileCleaned)
    }

    @Test
    fun `cleanAll should clean cache`() = runBlocking {
        var fileCleaned = false
        with(File("/tmp/kachej", OBJECT_CLEAN_ALL)) {
            parentFile.mkdirs()
            writeText("text")
            assertTrue(exists())
        }

        getKachej().cleanAll().collect { fileCleaned = true }

        assertTrue(fileCleaned)
    }

    @After
    fun tearDown() {
        File("/tmp/kachej").deleteRecursively()
    }

    private fun getKachej() = Kachej(parentDir = File("/tmp/kachej"))

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

private const val OBJECT_READ = "read.cache"
private const val OBJECT_WRITE = "write.cache"
private const val OBJECT_CLEAN_SINGLE = "clean.cache"
private const val OBJECT_CLEAN_ALL = "clean_all.cache"