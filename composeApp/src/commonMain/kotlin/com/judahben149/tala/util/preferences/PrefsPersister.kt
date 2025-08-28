package com.judahben149.tala.util.preferences

import com.russhwolf.settings.Settings

class PrefsPersister(
    private val settings: Settings
) {

    fun saveBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    fun fetchBoolean(key: String, defaultValue: Boolean): Boolean {
        return settings.getBoolean(key, defaultValue)
    }

    fun saveString(key: String, value: String) {
        settings.putString(key, value)
    }

    fun fetchString(key: String, defaultValue: String): String {
        return settings.getString(key, defaultValue)
    }

    fun saveInt(key: String, value: Int) {
        settings.putInt(key, value)
    }

    fun fetchInt(key: String, defaultValue: Int): Int {
        return settings.getInt(key, defaultValue)
    }

    fun saveLong(key: String, value: Long) {
        settings.putLong(key, value)
    }

    fun fetchLong(key: String, defaultValue: Long): Long {
        return settings.getLong(key, defaultValue)
    }

    fun saveStringSet(key: String, value: Set<String>) {
        val serializedSet = value.joinToString(separator = ",")
        settings.putString(key, serializedSet)
    }

    fun fetchStringSet(key: String, defaultValue: Set<String>): Set<String> {
        val serializedSet = settings.getString(key, "")
        return if (serializedSet.isEmpty()) {
            defaultValue
        } else {
            serializedSet.split(",").filter { it.isNotEmpty() }.toSet()
        }
    }

    fun removeKey(key: String) {
        settings.remove(key)
    }

    fun clearAll() {
        settings.clear()
    }

    fun hasKey(key: String): Boolean {
        return settings.hasKey(key)
    }
}