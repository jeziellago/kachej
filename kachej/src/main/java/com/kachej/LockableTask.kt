package com.kachej

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class LockableTask(private val coroutineContext: CoroutineContext = Dispatchers.IO) {

    private val locker = Mutex()

    suspend fun <T> execute(action: () -> T) =
        withContext(coroutineContext) {
            locker.withLock { action() }
        }
}
