package com.kachej

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.TimeUnit
import java.util.Date
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

object FileManager {

    @JvmStatic
    fun getFileFrom(cache: File, filename: String): File {
        return File(cache.absolutePath, filename).also { file ->
            check(file.exists()) { "File $filename not found." }
        }
    }

    @JvmStatic
    fun deleteFile(file: File) {
        check(file.delete()) { "Error deleting file ${file.name}." }
    }

    @JvmStatic
    fun cleanDir(dir: File) {
        dir.listFiles()?.forEach { file -> deleteFile(file) }
    }

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun cleanDirIfTimeToLiveHasExpired(
        dir: File,
        timeToLive: Long,
        timeUnit: TimeUnit
    ): Boolean {
        with(dir) {
            val creationTime = Files
                .readAttributes(toPath(), BasicFileAttributes::class.java)
                .creationTime()
                .toMillis()

            val currentTime = Date().time
            val cacheLifetime = (currentTime - creationTime)

            val shouldClearDir = (cacheLifetime > timeToLive.toDuration(timeUnit).inMilliseconds)
            if (shouldClearDir) cleanDir(dir)
            return shouldClearDir
        }
    }
}