@file:Suppress("UNCHECKED_CAST")

package com.achmadss.prodiainterview.data

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Extension functions for SharedPreferences
fun <T> SharedPreferences.asFlow(key: String, defaultValue: T?): Flow<T?> = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        if (changedKey == key) {
            trySend(get(changedKey, defaultValue) as T)
        }
    }
    registerOnSharedPreferenceChangeListener(listener)
    trySend(get(key, defaultValue) as T)
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}

fun SharedPreferences.put(key: String, value: Any?) {
    val editor = edit()
    when (value) {
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        is String -> editor.putString(key, value)
        is Float -> editor.putFloat(key, value)
        is Boolean -> editor.putBoolean(key, value)
        else -> throw IllegalArgumentException("Unsupported type")
    }
    editor.apply()
}

fun <T> SharedPreferences.get(key: String, defaultValue: T?): Any {
    return when (defaultValue) {
        is Int -> getInt(key, defaultValue)
        is Long -> getLong(key, defaultValue)
        is String -> getString(key, defaultValue) ?: defaultValue
        is Float -> getFloat(key, defaultValue)
        is Boolean -> getBoolean(key, defaultValue)
        else -> throw IllegalArgumentException("Unsupported type")
    }
}

