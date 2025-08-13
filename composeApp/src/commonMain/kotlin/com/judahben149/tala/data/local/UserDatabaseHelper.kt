package com.judahben149.tala.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.judahben149.tala.TalaDatabase
import com.judahben149.tala.Users
import com.judahben149.tala.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDatabaseHelper(driverFactory: DatabaseDriverFactory) {
    private val database: TalaDatabase = TalaDatabase(driverFactory.createDriver())
    private val userQueries = database.usersQueries

    fun getUserById(userId: String): Flow<UserEntity?> {
        return userQueries.getUserById(userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toUserEntity() }
    }

    suspend fun insertUser(user: UserEntity) {
        userQueries.insertUser(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
            photoUrl = user.photoUrl,
            isEmailVerified = if (user.isEmailVerified) 1L else 0L
        )
    }
}

// Mapping extension
private fun Users.toUserEntity() = UserEntity(
    id = id,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    isEmailVerified = isEmailVerified == 1L
)