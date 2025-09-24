package com.judahben149.tala.domain.managers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.haptics.HapticSettings
import com.judahben149.tala.domain.models.haptics.HapticType
import com.judahben149.tala.util.preferences.PrefsPersister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class PlatformHapticsManager actual constructor(
    private val context: Any?,
    private val settings: PrefsPersister,
    private val logger: Logger
) : HapticsManager {

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = (context as Context).getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            (context as Context).getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private var hapticSettings = HapticSettings()

    companion object {
        private const val HAPTIC_ENABLED_KEY = "haptic_enabled"
        private const val HAPTIC_INTENSITY_KEY = "haptic_intensity"
    }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        hapticSettings = hapticSettings.copy(
            enabled = settings.fetchBoolean(HAPTIC_ENABLED_KEY, true),
            intensity = settings.fetchFloat(HAPTIC_INTENSITY_KEY, 1.0f)
        )
    }

    override suspend fun trigger(hapticType: HapticType) {
        if (!isEnabled()) return

        withContext(Dispatchers.Main) {
            try {
                when (hapticType) {
                    HapticType.SELECTION -> performSelection()
                    HapticType.SUCCESS -> performSuccess()
                    HapticType.WARNING -> performWarning()
                    HapticType.ERROR -> performError()
                    HapticType.LIGHT_IMPACT -> performLightImpact()
                    HapticType.MEDIUM_IMPACT -> performMediumImpact()
                    HapticType.HEAVY_IMPACT -> performHeavyImpact()
                    HapticType.BUTTON_PRESS -> performButtonPress()
                    HapticType.TOGGLE_ON -> performToggleOn()
                    HapticType.TOGGLE_OFF -> performToggleOff()
                    HapticType.SWIPE -> performSwipe()
                    HapticType.SCROLL_TICK -> performScrollTick()
                    HapticType.NAVIGATION -> performNavigation()
                    HapticType.VOICE_START -> performVoiceStart()
                    HapticType.VOICE_STOP -> performVoiceStop()
                }
            } catch (e: Exception) {
                logger.e(e) { "Failed to trigger haptic feedback: ${hapticType.name}" }
            }
        }
    }

    private fun performSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }

    private fun performSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 100), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 50, 50, 100), -1)
        }
    }

    private fun performWarning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 30, 100), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 100, 30, 100), -1)
        }
    }

    private fun performError() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 200, 50, 200, 50, 300)
            val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 200, 50, 200, 50, 300), -1)
        }
    }

    private fun performLightImpact() {
        val duration = (10 * hapticSettings.intensity).toLong()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    private fun performMediumImpact() {
        val duration = (25 * hapticSettings.intensity).toLong()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    private fun performHeavyImpact() {
        val duration = (50 * hapticSettings.intensity).toLong()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    private fun performButtonPress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15)
        }
    }

    private fun performToggleOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 20, 50), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 30, 20, 50), -1)
        }
    }

    private fun performToggleOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }
    }

    private fun performSwipe() = performLightImpact()

    private fun performScrollTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(5)
        }
    }

    private fun performNavigation() = performMediumImpact()
    private fun performVoiceStart() = performSuccess()
    private fun performVoiceStop() = performMediumImpact()

    override suspend fun customPattern(pattern: LongArray, amplitudes: IntArray) {
        if (!isEnabled()) return

        withContext(Dispatchers.Main) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && amplitudes.isNotEmpty()) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            } catch (e: Exception) {
                logger.e(e) { "Failed to trigger custom haptic pattern" }
            }
        }
    }

    override fun isEnabled(): Boolean = hapticSettings.enabled

    override fun setEnabled(enabled: Boolean) {
        hapticSettings = hapticSettings.copy(enabled = enabled)
        settings.saveBoolean(HAPTIC_ENABLED_KEY, enabled)
    }
}
