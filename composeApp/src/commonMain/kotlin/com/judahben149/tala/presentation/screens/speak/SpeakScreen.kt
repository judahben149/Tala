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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.navigation.components.others.SpeakScreenComponent
import com.judahben149.tala.presentation.RequestAudioPermission
import com.judahben149.tala.presentation.screens.speak.components.MainActionButton
import com.judahben149.tala.presentation.shared_components.buttons.BackButton
import com.judahben149.tala.ui.theme.Black
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
import com.judahben149.tala.ui.theme.TalaColors
import com.judahben149.tala.ui.theme.White
import com.judahben149.tala.ui.theme.Yellow200
import com.judahben149.tala.ui.theme.Yellow300
import com.judahben149.tala.ui.theme.getTalaColors
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.min

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

    LaunchedEffect(Unit) {
        println("[DEBUG_LOG] SpeakScreen LaunchedEffect triggered")
        viewModel.updateSpeakingModes(component.speakingMode, component.scenario)
    }

    // Ensure conversation is initialized if permission is already granted
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            println("[DEBUG_LOG] Permission already granted, ensuring conversation is initialized")
            viewModel.onPermissionGranted()
        }
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        TopBar(
            onCloseClick = { component.goBack() },
            textColor = textColor,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Display conversation messages
        ConversationMessages(
            messages = uiState.messages,
            colors = colors,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 100.dp)
        )

        if (hasPermission) {
            MainActionButton(
                uiState = uiState,
                onClick = viewModel::onButtonClicked,
                onPress = viewModel::onButtonPressed,
                onRelease = viewModel::onButtonReleased,
                colors = colors,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            )

            Text(
                text = "Hold to record, release to send",
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
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
private fun TopBar(
    onCloseClick: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        BackButton(onClick = onCloseClick)
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

@Composable
private fun ConversationMessages(
    messages: List<ConversationMessage>,
    colors: TalaColors,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Debug: Print message count
    println("[DEBUG_LOG] ConversationMessages composable received ${messages.size} messages")
    if (messages.isNotEmpty()) {
        println("[DEBUG_LOG] First message: ${messages.first().content}")
        println("[DEBUG_LOG] Message IDs: ${messages.map { it.id }}")
        println("[DEBUG_LOG] Message conversation IDs: ${messages.map { it.conversationId }}")
        println("[DEBUG_LOG] Message is user: ${messages.map { it.isUser }}")
    } else {
        println("[DEBUG_LOG] No messages received in ConversationMessages composable")
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
            println("[DEBUG_LOG] Scrolling to item ${messages.size - 1}")
        }
    }

    // If no messages, show a placeholder
    if (messages.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No messages yet. Start talking!",
                color = colors.secondaryText.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.background(Color.Transparent), // Ensure background is transparent
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                MessageItem(
                    message = message,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: ConversationMessage,
    colors: TalaColors
) {
    val isUser = message.isUser
    val backgroundColor = if (isUser) colors.primary.copy(alpha = 0.15f) else colors.cardBackground
    val alignment = if (isUser) Arrangement.End else Arrangement.Start
    val horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.primaryText,
                    fontSize = 14.sp
                )
            }
        }
    }
}
