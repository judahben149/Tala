package com.judahben149.tala.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.judahben149.tala.data.local.dao.TestEntityDao
import com.judahben149.tala.data.local.dao.UserDao
import com.judahben149.tala.data.model.TestEntity
import com.judahben149.tala.data.model.UserEntity
import com.judahben149.tala.data.local.converter.DateTimeConverters

@Database(
    entities = [
        UserEntity::class,
        TestEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun testEntityDao(): TestEntityDao
}
