@file:Suppress("SpellCheckingInspection")

package com.kachej

import java.io.Serializable

data class CacheableMap(val map: Map<String, Serializable>) : Serializable
