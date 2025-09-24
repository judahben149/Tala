package com.judahben149.tala

import android.app.Application
import com.judahben149.tala.core.purchases.initRevenueCat
import com.judahben149.tala.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(appModule)

            androidContext(this@AndroidApplication)
        }

        initRevenueCat()
    }
}