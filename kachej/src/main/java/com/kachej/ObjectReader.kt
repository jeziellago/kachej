package com.kachej

import kotlinx.coroutines.Dispatchers
import java.io.Serializable
import kotlin.coroutines.CoroutineContext

interface ObjectReader {

    suspend fun <T : Serializable> read(
        filename: String,
        coroutineContext: CoroutineContext = Dispatchers.IO
    ): T?
}
