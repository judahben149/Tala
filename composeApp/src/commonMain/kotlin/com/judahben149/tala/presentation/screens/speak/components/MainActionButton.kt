package com.judahben149.tala.presentation.screens.speak.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.judahben149.tala.presentation.screens.speak.ConversationState
import com.judahben149.tala.presentation.screens.speak.SpeakScreenUiState
import com.judahben149.tala.ui.theme.TalaColors
import kotlinx.coroutines.delay

@Composable
fun MainActionButton(
    uiState: SpeakScreenUiState,
    onClick: () -> Unit,
    colors: TalaColors,
    modifier: Modifier = Modifier,
    onPress: () -> Unit = onClick,
    onRelease: () -> Unit = {}
) {
    val iconScale by animateFloatAsState(
        targetValue = when (uiState.conversationState) {
            ConversationState.Recording -> 1.1f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )

    val outlineAlpha by animateFloatAsState(
        targetValue = when (uiState.conversationState) {
            ConversationState.Converting, ConversationState.Thinking -> 0.3f
            else -> 1.0f
        },
        animationSpec = tween(300),
        label = "outlineAlpha"
    )

    Box(
        modifier = modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        // Static outline circle
        Box(
            modifier = Modifier
                .size(96.dp)
                .border(
                    width = 8.dp,
                    color = getOutlineColor(uiState.conversationState, colors).copy(alpha = outlineAlpha),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .pointerInput(uiState.isButtonEnabled) {
                    detectTapGestures(
                        onPress = {
                            if (uiState.isButtonEnabled) {
                                onPress()
                                tryAwaitRelease()
                                onRelease()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Icon with morphing animation
            AnimatedIcon(
                conversationState = uiState.conversationState,
                isLoading = uiState.isLoading,
                colors = colors,
                scale = iconScale
            )
        }

        // Spinning track overlay for thinking state
        if (uiState.conversationState == ConversationState.Thinking) {
            SpinningTrack(colors = colors)
        }
    }
}

@Composable
private fun AnimatedIcon(
    conversationState: ConversationState,
    isLoading: Boolean,
    colors: TalaColors,
    scale: Float
) {
    val currentIcon = getCurrentIcon(conversationState)
    var previousIcon by remember { mutableStateOf(currentIcon) }
    var showTransition by remember { mutableStateOf(false) }

    // Trigger transition when icon changes
    LaunchedEffect(currentIcon) {
        if (previousIcon != currentIcon) {
            showTransition = true
            delay(150) // Half of transition duration
            previousIcon = currentIcon
            showTransition = false
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(32.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = getIconTint(conversationState, colors),
                strokeWidth = 2.dp
            )
        } else {
            // Crossfade between icons for smooth morphing effect
            Crossfade(
                targetState = if (showTransition) previousIcon to currentIcon else currentIcon to currentIcon,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "iconCrossfade"
            ) { (prevIcon, currIcon) ->
                if (showTransition) {
                    // During transition, show both icons
                    Box {
                        Icon(
                            imageVector = prevIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .scale(scale * 0.8f)
                                .alpha(0.3f),
                            tint = getIconTint(conversationState, colors)
                        )
                        Icon(
                            imageVector = currIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .scale(scale * 1.2f)
                                .alpha(0.7f),
                            tint = getIconTint(conversationState, colors)
                        )
                    }
                } else {
                    // Normal state
                    Icon(
                        imageVector = currIcon,
                        contentDescription = getContentDescription(conversationState),
                        modifier = Modifier
                            .size(32.dp)
                            .scale(scale),
                        tint = getIconTint(conversationState, colors)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpinningTrack(colors: TalaColors) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinningTrack")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(
        modifier = Modifier.size(96.dp)
    ) {
        val strokeWidth = 2.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2

        rotate(rotation) {
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        colors.primary.copy(alpha = 0.2f),
                        colors.primary.copy(alpha = 0.6f),
                        colors.primary.copy(alpha = 1.0f),
                        colors.primary.copy(alpha = 0.6f),
                        colors.primary.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                radius = radius,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

private fun getCurrentIcon(conversationState: ConversationState): ImageVector {
    return when (conversationState) {
        ConversationState.Idle -> Icons.Default.Mic
        ConversationState.Recording -> Icons.Default.Stop
        ConversationState.Converting -> Icons.Default.VolumeUp
        ConversationState.Thinking -> Icons.Default.Psychology
        ConversationState.Speaking -> Icons.Default.VolumeOff
        ConversationState.Stopped -> Icons.Default.Refresh
        ConversationState.Disallowed -> Icons.Default.Warning
    }
}

private fun getOutlineColor(
    conversationState: ConversationState,
    colors: TalaColors
): Color {
    return when (conversationState) {
        ConversationState.Recording -> colors.errorText // Red outline for recording
        ConversationState.Speaking -> colors.primary
        else -> colors.primary
    }
}

private fun getIconTint(
    conversationState: ConversationState,
    colors: TalaColors
): Color {
    return when (conversationState) {
        ConversationState.Recording -> colors.errorText
        ConversationState.Thinking -> colors.primary.copy(alpha = 0.8f)
        else -> colors.primary
    }
}

private fun getContentDescription(conversationState: ConversationState): String {
    return when (conversationState) {
        ConversationState.Idle -> "Start Recording"
        ConversationState.Recording -> "Stop Recording"
        ConversationState.Converting -> "Converting Speech"
        ConversationState.Thinking -> "Processing"
        ConversationState.Speaking -> "Stop Speaking"
        ConversationState.Stopped -> "Restart"
        ConversationState.Disallowed -> "Conversation Disallowed"
    }
}
