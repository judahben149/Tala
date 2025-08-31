package com.judahben149.tala.domain.managers

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.haptics.HapticType
import com.judahben149.tala.util.preferences.PrefsPersister

interface HapticsManager {
    suspend fun trigger(hapticType: HapticType)
    suspend fun customPattern(pattern: LongArray, amplitudes: IntArray = intArrayOf())
    fun isEnabled(): Boolean
    fun setEnabled(enabled: Boolean)
}

expect class PlatformHapticsManager(
    context: Any?,
    settings: PrefsPersister,
    logger: Logger
)