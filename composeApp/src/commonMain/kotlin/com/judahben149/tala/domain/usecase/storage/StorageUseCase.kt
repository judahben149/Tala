package com.judahben149.tala.domain.usecase.storage

import com.judahben149.tala.domain.model.TestEntity
import com.judahben149.tala.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow

class StorageUseCase(
    private val storageRepository: StorageRepository
) {
    // Room operations
    suspend fun insertTestEntity(entity: TestEntity) {
        storageRepository.insertTestEntity(entity)
    }
    
    fun getAllTestEntities(): Flow<List<TestEntity>> {
        return storageRepository.getAllTestEntities()
    }
    
    suspend fun deleteTestEntity(entity: TestEntity) {
        storageRepository.deleteTestEntity(entity)
    }
    
    // DataStore operations
    suspend fun saveString(key: String, value: String) {
        storageRepository.saveString(key, value)
    }
    
    fun getString(key: String, defaultValue: String = ""): Flow<String> {
        return storageRepository.getString(key, defaultValue)
    }
    
    suspend fun saveInt(key: String, value: Int) {
        storageRepository.saveInt(key, value)
    }
    
    fun getInt(key: String, defaultValue: Int = 0): Flow<Int> {
        return storageRepository.getInt(key, defaultValue)
    }
}
