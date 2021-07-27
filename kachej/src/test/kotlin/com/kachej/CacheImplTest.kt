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

import app.cash.turbine.test
import com.kachej.type.CacheableList
import com.kachej.type.CacheableMap
import com.kachej.type.cacheOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.NotSerializableException
import kotlin.time.ExperimentalTime

private const val OBJECT_READ = "read.cache"
private const val OBJECT_WRITE = "write.cache"
private const val OBJECT_CLEAN_SINGLE = "clean.cache"
private const val OBJECT_CLEAN_ALL = "clean_all.cache"

@ExperimentalTime
class CacheImplTest {

    lateinit var cache: Cache

    @get:Rule
    val tmpFolder = TemporaryFolder()

    @Before
    fun setup() {
        cache = Cache.of(parentDir = tmpFolder.newFolder("kachej"))
    }

    @Test
    fun `writer should write serializable object with success`() = runBlocking {
        cache.put(OBJECT_WRITE, serializableObject).test {
            expectItem()
            expectComplete()
        }
    }

    @Test
    fun `writer should write map object with success`() = runBlocking {
        val expectedValue = cacheOf("name" to "username", "age" to 50)

        cache.put(OBJECT_WRITE, expectedValue).test {
            expectItem()
            expectComplete()
        }
    }

    @Test
    fun `writer should not write unserializable`() = runBlocking {
        cache.put(OBJECT_WRITE, unserializableObject).test {
            assertThat(expectError(), IsInstanceOf(NotSerializableException::class.java))
        }
    }

    @Test
    fun `reader should read object from file`() = runBlocking {
        cache.put(OBJECT_READ, serializableObject).first()

        cache.get<MyObject>(OBJECT_READ).test {
            assertEquals(serializableObject, expectItem())
            expectComplete()
        }
    }

    @Test
    fun `reader should read cacheable map from file`() = runBlocking {
        val expectedObject = cacheOf("key" to "value")

        cache.put(OBJECT_READ, expectedObject).first()

        cache.get<CacheableMap>(OBJECT_READ).test {
            assertEquals(expectedObject, expectItem())
            expectComplete()
        }
    }

    @Test
    fun `reader should read cacheable list from file`() = runBlocking {
        val expectedObject = cacheOf("1", 2, 3L)

        cache.put(OBJECT_READ, expectedObject).first()

        cache.get<CacheableList<*>>(OBJECT_READ).test {
            assertEquals(expectedObject, expectItem())
            expectComplete()
        }
    }

    @Test
    fun `clean file should clean object from cache`() = runBlocking {
        cache.put(OBJECT_CLEAN_SINGLE, CacheableMap(emptyMap())).first()

        cache.clear(OBJECT_CLEAN_SINGLE).test {
            expectItem()
            expectComplete()
        }
    }

    @Test
    fun `cleanAll should clean cache`() = runBlocking {
        cache.put(OBJECT_CLEAN_ALL, CacheableMap(emptyMap())).first()

        cache.clearAll().test {
            expectItem()
            expectComplete()
        }
    }

    @After
    fun tearDown() {
        tmpFolder.delete()
    }
}
