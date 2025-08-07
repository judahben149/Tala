package com.judahben149.tala.data.local.dao

import androidx.room.*
import com.judahben149.tala.data.model.TestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestEntityDao {
    @Query("SELECT * FROM test_entities ORDER BY createdAt DESC")
    fun getAllTestEntities(): Flow<List<TestEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestEntity(entity: TestEntity)
    
    @Delete
    suspend fun deleteTestEntity(entity: TestEntity)
    
    @Query("DELETE FROM test_entities")
    suspend fun deleteAllTestEntities()
}
