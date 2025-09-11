package com.judahben149.tala.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getCurrentDateString(tz: TimeZone = TimeZone.currentSystemDefault()): String {
    val now = Clock.System.now()
    val date = now.toLocalDateTime(tz).date
    return "${date.year}-${date.month.number.toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}"
}