package com.judahben149.tala.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.judahben149.tala.TalaDatabase

actual class DatabaseDriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = TalaDatabase.Schema,
            name = "TalaDatabase.db"
        )
    }
}