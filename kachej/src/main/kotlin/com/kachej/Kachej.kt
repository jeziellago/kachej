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

import com.kachej.FileManager.cleanDir
import com.kachej.FileManager.cleanDirIfTimeToLiveHasExpired
import com.kachej.FileManager.deleteFile
import com.kachej.FileManager.getFileFrom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.FileNotFoundException
import java.io.Serializable
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class Kachej(
    private val parentDir: File,
    private val timeToLive: Long = 3600,
    private val liveUnit: TimeUnit = TimeUnit.SECONDS
) : ObjectReader, ObjectWriter, ObjectCleaner {

    init {
        if (!parentDir.exists()) parentDir.mkdirs()
    }

    override fun <T : Serializable> write(
        filename: String,
        value: T
    ): Flow<Unit> = flow {
        emit(lockableTask {
            val file = File(parentDir.absolutePath, filename)
            ObjectOutputStream(file.outputStream()).run {
                writeObject(value)
                close()
            }
        })
    }

    override fun <T : Serializable> read(filename: String): Flow<T> =
        flow {
            emit(lockableTask {
                if (cleanDirIfTimeToLiveHasExpired(parentDir, timeToLive, liveUnit))
                    throw FileNotFoundException("File $filename not found.")
                val file = getFileFrom(parentDir, filename)
                ObjectInputStream(file.inputStream()).use { reader ->
                    @Suppress("UNCHECKED_CAST")
                    reader.readObject() as T
                }
            })
        }

    override fun clean(filename: String): Flow<Unit> =
        flow {
            emit(lockableTask {
                getFileFrom(parentDir, filename)
                    .run { deleteFile(this) }
            })
        }

    override fun cleanAll(): Flow<Unit> =
        flow {
            emit(lockableTask {
                cleanDir(parentDir)
            })
        }

}
