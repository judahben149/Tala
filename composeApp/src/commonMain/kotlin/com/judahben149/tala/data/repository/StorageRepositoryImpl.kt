package com.judahben149.tala.data.repository

import com.judahben149.tala.data.local.dao.TestEntityDao
import com.judahben149.tala.data.model.TestEntity
import com.judahben149.tala.domain.model.TestEntity as DomainTestEntity
import com.judahben149.tala.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class StorageRepositoryImpl(
    private val testEntityDao: TestEntityDao,
    private val dataStore: DataStore
) : StorageRepository {
    
    override suspend fun insertTestEntity(entity: DomainTestEntity) {
        val dataEntity = TestEntity(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdAt
        )
        testEntityDao.insertTestEntity(dataEntity)
    }
    
    override fun getAllTestEntities(): Flow<List<DomainTestEntity>> {
        return testEntityDao.getAllTestEntities().map { entities ->
            entities.map { entity ->
                DomainTestEntity(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    createdAt = entity.createdAt
                )
            }
        }
    }
    
    override suspend fun deleteTestEntity(entity: DomainTestEntity) {
        val dataEntity = TestEntity(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdAt
        )
        testEntityDao.deleteTestEntity(dataEntity)
    }
    
    override suspend fun saveString(key: String, value: String) {
        dataStore.saveString(key, value)
    }
    
    override fun getString(key: String, defaultValue: String): Flow<String> {
        return dataStore.getString(key, defaultValue)
    }
    
    override suspend fun saveInt(key: String, value: Int) {
        dataStore.saveInt(key, value)
    }
    
    override fun getInt(key: String, defaultValue: Int): Flow<Int> {
        return dataStore.getInt(key, defaultValue)
    }
}
