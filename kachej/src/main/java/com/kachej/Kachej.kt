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

import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import kotlin.coroutines.CoroutineContext

class Kachej(
    private val parentDir: File = File(".")
) : ObjectReader, ObjectWriter {

    override suspend fun <T : Serializable> write(
        filename: String,
        value: T,
        coroutineContext: CoroutineContext
    ): Boolean {
        return LockableTask(coroutineContext).execute {
            val file = File(parentDir.absolutePath, filename)
            if (file.exists()) file.delete()
            ObjectOutputStream(file.outputStream()).use {
                it.writeObject(value)
            }
            true
        }
    }

    override suspend fun <T : Serializable> read(
        filename: String,
        coroutineContext: CoroutineContext
    ): T? = LockableTask(coroutineContext).execute {
        val file = File(parentDir.absolutePath, filename)
        if (!file.exists()) return@execute null
        ObjectInputStream(file.inputStream()).use { reader ->
            @Suppress("UNCHECKED_CAST")
            reader.readObject() as T
        }
    }
}
