@file:Suppress("UNCHECKED_CAST")

package com.achmadss.prodiainterview.data.common

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        is List<*> -> {
            if (value.all { it is String }) {
                editor.putStringSet(key, value.map { it as String }.toSet())
            } else {
                throw IllegalArgumentException("Only List<String> is supported")
            }
        }
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
        is List<*> -> getStringSet(key, (defaultValue as? List<String>)?.toSet())
            ?.toList() ?: defaultValue
        else -> throw IllegalArgumentException("Unsupported type")
    }
}