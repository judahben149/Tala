package com.judahben149.tala.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.judahben149.tala.data.dao.UserDao
import com.judahben149.tala.data.model.UserEntity

internal expect object TalaDatabaseCtor : RoomDatabaseConstructor<TalaDatabase>

@Database(entities = [UserEntity::class], version = 1)
@ConstructedBy(TalaDatabaseCtor::class)
abstract class TalaDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}