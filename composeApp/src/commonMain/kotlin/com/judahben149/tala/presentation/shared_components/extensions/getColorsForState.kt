package com.judahben149.tala.presentation.shared_components.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.judahben149.tala.presentation.screens.speak.ConversationState
import com.judahben149.tala.ui.theme.Green300
import com.judahben149.tala.ui.theme.Green400
import com.judahben149.tala.ui.theme.Green500
import com.judahben149.tala.ui.theme.Green600
import com.judahben149.tala.ui.theme.Green700
import com.judahben149.tala.ui.theme.Red400
import com.judahben149.tala.ui.theme.Red500
import com.judahben149.tala.ui.theme.Red600
import com.judahben149.tala.ui.theme.Red700
import com.judahben149.tala.ui.theme.Sky400
import com.judahben149.tala.ui.theme.Sky500
import com.judahben149.tala.ui.theme.Sky600
import com.judahben149.tala.ui.theme.Yellow200
import com.judahben149.tala.ui.theme.Yellow300

@Composable
private fun getColorsForState(state: ConversationState): Triple<List<Color>, List<Color>, List<Color>> {
    return when (state) {
        ConversationState.Idle -> Triple(
            listOf(Green400, Sky500), // Keep original colors for idle
            listOf(Yellow200, Sky500),
            listOf(Green400, Sky500)
        )
        ConversationState.Recording -> Triple(
            listOf(Red500, Red600), // Red tones for recording
            listOf(Red400, Red500),
            listOf(Red600, Red700)
        )
        ConversationState.Converting -> Triple(
            listOf(Sky500, Sky500), // Blue for processing
            listOf(Sky400, Sky500),
            listOf(Sky500, Sky600)
        )
        ConversationState.Thinking -> Triple(
            listOf(Yellow200, Green400), // Yellow-green for thinking
            listOf(Yellow200, Green400),
            listOf(Yellow300, Green500)
        )
        ConversationState.Speaking -> Triple(
            listOf(Green400, Green600), // Green for active speaking
            listOf(Green300, Green500),
            listOf(Green500, Green700)
        )
        ConversationState.Stopped -> Triple(
            listOf(Red500, Yellow200), // Red-yellow for stopped/paused
            listOf(Red400, Yellow200),
            listOf(Red600, Yellow300)
        )

        ConversationState.Disallowed -> Triple(
            listOf(Red500, Red600), // Red for quota exceeded
            listOf(Red400, Red500),
            listOf(Red600, Red700)
        )
    }
}