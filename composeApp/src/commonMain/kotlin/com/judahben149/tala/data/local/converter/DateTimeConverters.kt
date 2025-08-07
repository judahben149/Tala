package com.judahben149.tala.data.local.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class DateTimeConverters {
    @TypeConverter
    fun fromInstant(instant: Instant?): String? {
        return instant?.toString()
    }
    
    @TypeConverter
    fun toInstant(value: String?): Instant? {
        return value?.let { Instant.parse(it) }
    }
}
