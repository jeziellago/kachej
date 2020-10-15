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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.NotSerializableException
import java.io.Serializable

@RunWith(JUnit4::class)
class KachejTest {

    lateinit var kachej: Kachej

    @get:Rule
    val tmpFolder = TemporaryFolder()

    @Before
    fun setup() {
        kachej = Kachej(parentDir = tmpFolder.newFolder("kachej"))
    }

    @Test
    fun `writer should write serializable object with success`() = runBlocking {
        var success = false

        kachej.write(OBJECT_WRITE, serializableObject) {
            onSuccess { success = true }
        }

        assert(success)
    }

    @Test
    fun `writer should write map object with success`() = runBlocking {
        val expectedValue = CacheableMap(mapOf("name" to "username", "age" to 50))

        var result = false

        kachej.write(OBJECT_WRITE, expectedValue) {
            onSuccess { result = true }
        }

        assert(result)
    }

    @Test
    fun `writer should rewrite serializable object with success`() = runBlocking {
        var success = false
        kachej.write(OBJECT_WRITE, serializableObject)

        kachej.write(OBJECT_WRITE, serializableObject) {
            onSuccess { success = true }
        }

        assert(success)
    }

    @Test
    fun `writer should not write unserializable`() = runBlocking {
        var exception: Throwable? = null
        kachej.write(OBJECT_WRITE, unserializableObject) {
            onFailure { exception = it }
        }

        assert(exception is NotSerializableException)
    }

    @Test
    fun `reader should read object from file`() = runBlocking {
        kachej.write(OBJECT_READ, serializableObject)

        var result: Any? = null

        kachej.read<MyObject>(OBJECT_READ) {
            onSuccess { result = it }
            onFailure { result = it }
        }

        assertEquals(serializableObject, result)
    }

    @Test
    fun `reader should read cacheable map from file`() = runBlocking {
        val expectedObject = CacheableMap(mapOf("key" to "value"))

        kachej.write(OBJECT_READ, expectedObject)

        var result: CacheableMap? = null

        kachej.read<CacheableMap>(OBJECT_READ) {
            onSuccess { result = it }
        }

        assertEquals(expectedObject, result)
    }

    @Test
    fun `reader should read cacheable list from file`() = runBlocking {
        val expectedObject = CacheableList(listOf("1", 2, 3L))

        kachej.write(OBJECT_READ, expectedObject)

        var result: CacheableList<*>? = null

        kachej.read<CacheableList<*>>(OBJECT_READ) {
            onSuccess { result = it }
        }

        assertEquals(expectedObject, result)
    }

    @Test
    fun `clean file should clean object from cache`() = runBlocking {
        kachej.write(OBJECT_CLEAN_SINGLE, CacheableMap(emptyMap()))

        kachej.clean(OBJECT_CLEAN_SINGLE)
    }

    @Test
    fun `cleanAll should clean cache`() = runBlocking {
        kachej.write(OBJECT_CLEAN_ALL, CacheableMap(emptyMap()))

        kachej.cleanAll()
    }

    @After
    fun tearDown() {
        tmpFolder.delete()
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

private const val OBJECT_READ = "read.cache"
private const val OBJECT_WRITE = "write.cache"
private const val OBJECT_CLEAN_SINGLE = "clean.cache"
private const val OBJECT_CLEAN_ALL = "clean_all.cache"