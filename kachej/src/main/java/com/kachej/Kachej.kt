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
