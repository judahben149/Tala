package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.model.TestEntity
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    // Room operations
    suspend fun insertTestEntity(entity: TestEntity)
    fun getAllTestEntities(): Flow<List<TestEntity>>
    suspend fun deleteTestEntity(entity: TestEntity)
    
    // DataStore operations
    suspend fun saveString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): Flow<String>
    suspend fun saveInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Flow<Int>
}
