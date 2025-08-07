package com.judahben149.tala.data.local

import android.content.Context
import com.judahben149.tala.platform.ContextProvider

actual fun createDataStore(): DataStore {
    val context = ContextProvider.getContext()
    return DataStoreImpl(context)
}
