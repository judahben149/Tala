package com.judahben149.tala.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.judahben149.tala.data.TalaDatabase
import kotlinx.coroutines.Dispatchers

fun getTalaDatabaseBuilder(context: Context): TalaDatabase {
    val dbFile = context.getDatabasePath("tala.db")

    return Room.databaseBuilder<TalaDatabase>(context, dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}