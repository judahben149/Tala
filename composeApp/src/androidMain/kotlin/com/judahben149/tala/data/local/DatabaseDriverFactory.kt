package com.judahben149.tala.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.judahben149.tala.TalaDatabase

actual class DatabaseDriverFactory(private val context: Context) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = TalaDatabase.Schema,
            context = context,
            name = "TalaDatabase.db"
        )
    }
}