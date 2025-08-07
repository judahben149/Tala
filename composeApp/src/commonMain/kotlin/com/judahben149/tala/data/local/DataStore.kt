package com.judahben149.tala.data.local

import kotlinx.coroutines.flow.Flow

interface DataStore {
    suspend fun saveString(key: String, value: String)
    fun getString(key: String, defaultValue: String): Flow<String>
    suspend fun saveInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int): Flow<Int>
}
