@file:Suppress("SpellCheckingInspection")

package com.kachej

import java.io.Serializable

data class CacheableList<T: Serializable>(val items: Collection<T>) : Serializable