package com.judahben149.tala.data.local

actual fun createDataStore(): DataStore {
    return DataStoreImpl()
}
