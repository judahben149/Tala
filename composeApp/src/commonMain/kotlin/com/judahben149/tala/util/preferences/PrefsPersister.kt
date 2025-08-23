package com.judahben149.tala.util.preferences

import com.russhwolf.settings.Settings

class PrefsPersister(
    private val settings: Settings
) {

    fun saveBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    fun loadBoolean(key: String, defaultValue: Boolean): Boolean {
        return settings.getBoolean(key, defaultValue)
    }

    fun saveString(key: String, value: String) {
        settings.putString(key, value)
    }

    fun loadString(key: String, defaultValue: String): String {
        return settings.getString(key, defaultValue)
    }

    fun saveInt(key: String, value: Int) {
        settings.putInt(key, value)
    }

    fun loadInt(key: String, defaultValue: Int): Int {
        return settings.getInt(key, defaultValue)
    }

    fun saveLong(key: String, value: Long) {
        settings.putLong(key, value)
    }

    fun loadLong(key: String, defaultValue: Long): Long {
        return settings.getLong(key, defaultValue)
    }
}