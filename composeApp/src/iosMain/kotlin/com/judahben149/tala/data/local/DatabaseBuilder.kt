package com.judahben149.tala.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.judahben149.tala.data.TalaDatabase
import platform.Foundation.NSHomeDirectory

fun getTalaDatabaseBuilder(): TalaDatabase {
    val dbFile = "${NSHomeDirectory()}/tala.db"

    return Room.databaseBuilder<TalaDatabase>(
        name = dbFile,
    )
        .setDriver(BundledSQLiteDriver())
//        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}