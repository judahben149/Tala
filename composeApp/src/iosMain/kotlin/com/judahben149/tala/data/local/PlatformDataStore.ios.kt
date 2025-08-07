package com.judahben149.tala.data.local

actual fun createDataStore(): TalaDataStore {
    return DataStoreImpl()
}
