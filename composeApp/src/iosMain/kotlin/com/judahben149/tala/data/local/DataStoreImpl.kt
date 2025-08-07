package com.judahben149.tala.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.Foundation.NSUserDefaults

class DataStoreImpl : TalaDataStore {
    
    override suspend fun saveString(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setObject(value, key)
    }
    
    override fun getString(key: String, defaultValue: String): Flow<String> {
        return flow {
            val value = NSUserDefaults.standardUserDefaults.stringForKey(key)
            emit(value ?: defaultValue)
        }
    }
    
    override suspend fun saveInt(key: String, value: Int) {
        NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), key)
    }
    
    override fun getInt(key: String, defaultValue: Int): Flow<Int> {
        return flow {
            val value = NSUserDefaults.standardUserDefaults.integerForKey(key)
            emit(value.toInt())
        }
    }
}
