package com.judahben149.tala.presentation.screens.history.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.navigation.components.others.ConversationDetailScreenComponent
import com.judahben149.tala.ui.theme.TalaColors
import com.judahben149.tala.ui.theme.getTalaColors
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationDetailScreen(
    component: ConversationDetailScreenComponent,
    viewModel: ConversationDetailViewModel = koinInject { parametersOf(component.conversationId) }
) {
    val uiState = viewModel.uiState.collectAsState()
    val colors = getTalaColors()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.value.messages) {
        component.conversationId?.let { id ->
            viewModel.loadConversation(id)
            if (uiState.value.messages.isNotEmpty()) {
                listState.scrollToItem(uiState.value.messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.value.conversation?.title ?: "Conversation",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.primaryText,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { component.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.appBackground,
                    titleContentColor = colors.primaryText,
                    navigationIconContentColor = colors.primaryText
                )
            )
        },
        containerColor = colors.appBackground
    ) { paddingValues ->
        ConversationDetailContent(
            state = uiState.value,
            onPlayUserAudio = { message -> viewModel.playUserAudio(message) },
            onPlayAiAudio = { message -> viewModel.playAiAudio(message) },
            onStopAudio = { viewModel.stopAudio() },
            onRetry = { viewModel.retryLoad() },
            onClearError = { viewModel.clearError() },
            onClearAudioError = { viewModel.clearAudioError() },
            colors = colors,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ConversationDetailContent(
    state: ConversationDetailState,
    onPlayUserAudio: (ConversationMessage) -> Unit,
    onPlayAiAudio: (ConversationMessage) -> Unit,
    onStopAudio: () -> Unit,
    onRetry: () -> Unit,
    onClearError: () -> Unit,
    onClearAudioError: () -> Unit,
    colors: TalaColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    color = colors.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.error != null -> {
                ErrorContent(
                    message = state.error,
                    onRetry = onRetry,
                    onDismiss = onClearError,
                    colors = colors
                )
            }

            state.messages.isNotEmpty() -> {
                ConversationMessages(
                    messages = state.messages,
                    currentlyPlayingMessageId = state.currentlyPlayingMessageId,
                    isLoadingAudio = state.isLoadingAudio,
                    audioError = state.audioError,
                    onPlayUserAudio = onPlayUserAudio,
                    onPlayAiAudio = onPlayAiAudio,
                    onStopAudio = onStopAudio,
                    onClearAudioError = onClearAudioError,
                    colors = colors
                )
            }

            else -> {
                Text(
                    text = "No messages found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.secondaryText,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun ConversationMessages(
    messages: List<ConversationMessage>,
    currentlyPlayingMessageId: String?,
    isLoadingAudio: Boolean,
    audioError: String?,
    onPlayUserAudio: (ConversationMessage) -> Unit,
    onPlayAiAudio: (ConversationMessage) -> Unit,
    onStopAudio: () -> Unit,
    onClearAudioError: () -> Unit,
    colors: TalaColors
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(messages, key = { it.id }) { message ->
            MessageItem(
                message = message,
                isPlaying = message.id == currentlyPlayingMessageId,
                isLoadingAudio = isLoadingAudio && message.id == currentlyPlayingMessageId,
                onPlayAudio = {
                    if (message.isUser) {
                        onPlayUserAudio(message)
                    } else {
                        onPlayAiAudio(message)
                    }
                },
                onStopAudio = onStopAudio,
                colors = colors
            )
        }
    }

    if (audioError != null) {
        AlertDialog(
            onDismissRequest = onClearAudioError,
            confirmButton = {
                TextButton(onClick = onClearAudioError) {
                    Text("OK", color = colors.primary)
                }
            },
            text = {
                Text(audioError, color = colors.errorText)
            },
            containerColor = colors.cardBackground,
            titleContentColor = colors.primaryText,
            textContentColor = colors.primaryText
        )
    }
}

@Composable
private fun MessageItem(
    message: ConversationMessage,
    isPlaying: Boolean,
    isLoadingAudio: Boolean,
    onPlayAudio: () -> Unit,
    onStopAudio: () -> Unit,
    colors: TalaColors
) {
    val isUser = message.isUser
    val backgroundColor = if (isUser) colors.primary.copy(alpha = 0.15f) else colors.cardBackground
    val alignment = if (isUser) Arrangement.End else Arrangement.Start
    val horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor,
            modifier = Modifier
                .widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.primaryText,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (message.userAudioPath != null || message.aiAudioPath != null) {
                    Row(
                        horizontalArrangement = horizontalArrangement,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoadingAudio) {
                            CircularProgressIndicator(
                                color = colors.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Button(
                                onClick = if (isPlaying) onStopAudio else onPlayAudio,
                                enabled = !isLoadingAudio,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primary,
                                    contentColor = colors.primaryButtonText
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isPlaying) "Stop" else "Play", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    colors: TalaColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.errorText,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.primaryButtonText
                )
            ) {
                Text("Retry")
            }
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondaryButtonBackground,
                    contentColor = colors.secondaryButtonText
                )
            ) {
                Text("Dismiss")
            }
        }
    }
}