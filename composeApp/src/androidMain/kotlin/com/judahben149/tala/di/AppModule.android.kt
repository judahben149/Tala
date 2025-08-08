package com.judahben149.tala.di

import android.content.Context
import com.judahben149.tala.data.TalaDatabase
import com.judahben149.tala.data.local.getTalaDatabaseBuilder
import org.koin.dsl.module

fun androidAppModule(context: Context) = module {
    single<TalaDatabase> { getTalaDatabaseBuilder(context) }
}