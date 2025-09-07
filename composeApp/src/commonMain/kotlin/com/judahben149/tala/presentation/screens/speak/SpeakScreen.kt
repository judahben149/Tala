package com.judahben149.tala.presentation.screens.speak

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.navigation.components.others.SpeakScreenComponent
import com.judahben149.tala.presentation.RequestAudioPermission
import com.judahben149.tala.presentation.screens.speak.components.MainActionButton
import com.judahben149.tala.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.min
import kotlin.random.Random

@Composable
fun SpeakScreen(
    component: SpeakScreenComponent,
    viewModel: SpeakScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var hasPermission by remember { mutableStateOf(false) }
    val currentVoiceLevel = uiState.voiceLevelForAnimation
    var hasAnimatedIn by remember { mutableStateOf(false) }

    val backgroundColor = if (isSystemInDarkTheme()) Black else White
    val textColor = if (isSystemInDarkTheme()) White else Black
    val colors = getTalaColors()

    // Color schemes based on conversation state
    val (shadowColors, borderColors, innerShadowColors) = getColorsForState(uiState.conversationState)

    // Animated colors with smooth transitions
    val animatedShadowColor1 by animateColorAsState(
        targetValue = shadowColors[0],
        animationSpec = tween(durationMillis = 800),
        label = "shadowColor1Animation"
    )
    val animatedShadowColor2 by animateColorAsState(
        targetValue = shadowColors[1],
        animationSpec = tween(durationMillis = 800),
        label = "shadowColor2Animation"
    )

    val animatedBorderColor1 by animateColorAsState(
        targetValue = borderColors[0],
        animationSpec = tween(durationMillis = 800),
        label = "borderColor1Animation"
    )
    val animatedBorderColor2 by animateColorAsState(
        targetValue = borderColors[1],
        animationSpec = tween(durationMillis = 800),
        label = "borderColor2Animation"
    )

    val animatedInnerShadowColor1 by animateColorAsState(
        targetValue = innerShadowColors[0],
        animationSpec = tween(durationMillis = 800),
        label = "innerShadowColor1Animation"
    )
    val animatedInnerShadowColor2 by animateColorAsState(
        targetValue = innerShadowColors[1],
        animationSpec = tween(durationMillis = 800),
        label = "innerShadowColor2Animation"
    )

    // Animation for initial spring entrance
    LaunchedEffect(Unit) {
        hasAnimatedIn = true
    }

    // Request permission when screen opens
    RequestAudioPermission { granted ->
        hasPermission = granted
        if (granted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

//    LaunchedEffect(uiState.conversationState == ConversationState.Recording) {
//        if (uiState.conversationState == ConversationState.Recording) {
//            while (true) {
//                currentVoiceLevel = Random.nextFloat() * (0.85f - 0.3f) + 0.3f
//                withFrameNanos { }
//                kotlinx.coroutines.delay(Random.nextLong(100, 500))
//            }
//        } else {
//            currentVoiceLevel = 0.3f
//        }
//    }

    val animatedVoiceLevel by animateFloatAsState(
        targetValue = currentVoiceLevel,
        label = "voiceLevelAnimation"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Responsive sizing based on available space
        val availableWidth = maxWidth
        val availableHeight = maxHeight

        // Calculate responsive dimensions (use smaller dimension to ensure it fits)
        val baseSize = min(availableWidth.value, availableHeight.value).dp
        val squircleWidth = baseSize * 0.8f
        val squircleHeight = baseSize * 1f
        val cornerRadius = squircleWidth * 0.25f

        val squircleShape = RoundedCornerShape(cornerRadius)

        // Animated size with spring effect on entrance and voice level variations
        val targetSizeMultiplier = if (!hasAnimatedIn) 0.5f else 1f + (animatedVoiceLevel - 0.3f) * 0.1f

        val animatedWidth by animateDpAsState(
            targetValue = squircleWidth * targetSizeMultiplier,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "squircleWidthAnimation"
        )

        val animatedHeight by animateDpAsState(
            targetValue = squircleHeight * targetSizeMultiplier,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "squircleHeightAnimation"
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-50).dp)
                .dropShadow(shape = squircleShape) {
                    radius = 60f
                    color = Red500
                    brush = Brush.verticalGradient(
                        colors = listOf(animatedShadowColor1, animatedShadowColor2)
                    )
                }
                .border(
                    width = 1.dp,
                    shape = squircleShape,
                    brush = Brush.verticalGradient(
                        colors = listOf(animatedBorderColor1, animatedBorderColor2)
                    ),
                )
                .size(width = animatedWidth, height = animatedHeight)
                .background(
                    color = backgroundColor,
                    shape = squircleShape,
                )
                .innerShadow(shape = squircleShape) {
                    radius = 90f
                    color = Red600
                    brush = Brush.verticalGradient(
                        colors = listOf(animatedInnerShadowColor1, animatedInnerShadowColor2)
                    )
                    alpha = .4f
                }
        )

        TopBar(
            onCloseClick = { component.goBack() },
            textColor = textColor,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (hasPermission) {
            MainActionButton(
                uiState = uiState,
                onClick = viewModel::onButtonClicked,
                colors = colors,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
            )

            Text(
                text = uiState.buttonLabel,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp)
            )
        }

        // Error display
        uiState.error?.let { error ->
            ErrorDisplay(
                error = error,
                colors = colors,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 100.dp)
                    .padding(horizontal = 24.dp)
            )
        }
    }
}

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
    }
}

@Composable
private fun TopBar(
    onCloseClick: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = onCloseClick
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ErrorDisplay(
    error: String,
    colors: TalaColors,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.textFieldErrorBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = error,
            color = colors.errorText,
            fontSize = 14.sp,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
        )
    }
}
