package com.judahben149.tala.platform

import android.content.Context

object ContextProvider {
    private var context: Context? = null
    
    fun setContext(context: Context) {
        this.context = context.applicationContext
    }
    
    fun getContext(): Context {
        return context ?: throw IllegalStateException("Context not initialized. Call setContext() first.")
    }
}
