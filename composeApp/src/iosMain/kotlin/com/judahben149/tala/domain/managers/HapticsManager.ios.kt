package com.judahben149.tala.domain.managers

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.haptics.HapticSettings
import com.judahben149.tala.domain.models.haptics.HapticType
import com.judahben149.tala.util.preferences.PrefsPersister
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
actual class PlatformHapticsManager actual constructor(
    private val context: Any?,
    private val settings: PrefsPersister,
    private val logger: Logger
) : HapticsManager {

    private val selectionGenerator by lazy { UISelectionFeedbackGenerator() }
    private val notificationGenerator by lazy { UINotificationFeedbackGenerator() }
    private val lightImpactGenerator by lazy { UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight) }
    private val mediumImpactGenerator by lazy { UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium) }
    private val heavyImpactGenerator by lazy { UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy) }

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
        selectionGenerator.prepare()
        selectionGenerator.selectionChanged()
    }

    private fun performSuccess() {
        notificationGenerator.prepare()
        notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
    }

    private fun performWarning() {
        notificationGenerator.prepare()
        notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeWarning)
    }

    private fun performError() {
        notificationGenerator.prepare()
        notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
    }

    private fun performLightImpact() {
        lightImpactGenerator.prepare()
        lightImpactGenerator.impactOccurred()
    }

    private fun performMediumImpact() {
        mediumImpactGenerator.prepare()
        mediumImpactGenerator.impactOccurred()
    }

    private fun performHeavyImpact() {
        heavyImpactGenerator.prepare()
        heavyImpactGenerator.impactOccurred()
    }

    private fun performButtonPress() = performSelection()
    private fun performToggleOn() = performSuccess()
    private fun performToggleOff() = performLightImpact()
    private fun performSwipe() = performLightImpact()
    private fun performScrollTick() = performSelection()
    private fun performNavigation() = performMediumImpact()
    private fun performVoiceStart() = performSuccess()
    private fun performVoiceStop() = performMediumImpact()

    override suspend fun customPattern(pattern: LongArray, amplitudes: IntArray) {
        // iOS doesn't support custom patterns, fallback to heavy impact
        performHeavyImpact()
    }

    override fun isEnabled(): Boolean = hapticSettings.enabled

    override fun setEnabled(enabled: Boolean) {
        hapticSettings = hapticSettings.copy(enabled = enabled)
        settings.saveBoolean(HAPTIC_ENABLED_KEY, enabled)
    }
}
