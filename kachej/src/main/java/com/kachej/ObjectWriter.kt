package com.kachej

import kotlinx.coroutines.Dispatchers
import java.io.Serializable
import kotlin.coroutines.CoroutineContext

interface ObjectWriter {

    suspend fun <T : Serializable> write(
        filename: String,
        value: T,
        coroutineContext: CoroutineContext = Dispatchers.IO
    ): Boolean
}
