package com.judahben149.tala.data.local

import com.judahben149.tala.platform.ContextProvider

actual fun createDataStore(): TalaDataStore {
    val context = ContextProvider.getContext()
    return DataStoreImpl(context)
}
