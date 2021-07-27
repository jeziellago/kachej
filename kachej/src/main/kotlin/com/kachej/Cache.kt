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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.*
import java.util.concurrent.TimeUnit

interface Cache {
    fun <T : Serializable> get(key: String): Flow<T>
    fun <T : Serializable> put(key: String, value: T): Flow<Unit>
    fun clear(key: String): Flow<Unit>
    fun clearAll(): Flow<Unit>

    companion object {
        fun of(
            parentDir: File,
            timeToLive: Long = 3600,
            liveUnit: TimeUnit = TimeUnit.SECONDS,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): Cache = CacheImpl(parentDir, timeToLive, liveUnit, dispatcher)
    }
}

internal class CacheImpl(
    private val parentDir: File,
    private val timeToLive: Long = 3600,
    private val liveUnit: TimeUnit = TimeUnit.SECONDS,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Cache {

    init {
        if (!parentDir.exists()) parentDir.mkdirs()
    }

    override fun <T : Serializable> get(key: String): Flow<T> =
        flow {
            val task = {
                if (FileManager.cleanDirIfTimeToLiveHasExpired(parentDir, timeToLive, liveUnit))
                    throw FileNotFoundException("File $key not found.")
                val file = FileManager.getFileFrom(parentDir, key)
                val ois = ObjectInputStream(file.inputStream())
                ois.use { reader ->
                    @Suppress("UNCHECKED_CAST")
                    reader.readObject() as T
                }
            }
            emit(task())
        }.flowOn(dispatcher)

    override fun <T : Serializable> put(key: String, value: T): Flow<Unit> =
        flow {
            val task = {
                val file = File(parentDir.absolutePath, key)
                val oos = ObjectOutputStream(file.outputStream())
                oos.writeObject(value)
                oos.close()
            }
            emit(task())
        }.flowOn(dispatcher)

    override fun clear(key: String): Flow<Unit> =
        flow {
            val task = {
                val file = FileManager.getFileFrom(parentDir, key)
                FileManager.deleteFile(file)
            }
            emit(task())
        }.flowOn(dispatcher)

    override fun clearAll(): Flow<Unit> =
        flow { emit(FileManager.cleanDir(parentDir)) }.flowOn(dispatcher)

}
