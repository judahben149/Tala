package com.judahben149.tala.data.local

import java.util.UUID

actual fun generateUUID(): String = UUID.randomUUID().toString()

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()