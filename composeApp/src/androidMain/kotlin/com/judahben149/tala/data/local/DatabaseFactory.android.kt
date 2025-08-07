package com.judahben149.tala.data.local

import android.content.Context
import androidx.room.Room
import com.judahben149.tala.platform.ContextProvider

actual fun createDatabase(): AppDatabase {
    val context = ContextProvider.getContext()
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "tala_database"
    ).build()
}
