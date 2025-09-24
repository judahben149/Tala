package com.judahben149.tala.presentation.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.navigation.components.others.ConversationListScreenComponent
import com.judahben149.tala.ui.theme.TalaColors
import com.judahben149.tala.ui.theme.getTalaColors
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    component: ConversationListScreenComponent,
    viewModel: ConversationHistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = getTalaColors()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Conversation History",
                        color = colors.primaryText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = component::goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refreshConversations) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = colors.primaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.appBackground
                )
            )
        },
        containerColor = colors.appBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState(colors = colors)
                }
                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error!!,
                        onRetry = viewModel::refreshConversations,
                        onDismiss = viewModel::clearError,
                        colors = colors
                    )
                }
                uiState.conversations.isEmpty() -> {
                    EmptyState(colors = colors)
                }
                else -> {
                    ConversationList(
                        conversations = uiState.conversations,
                        onConversationClick = { conversation ->
                            component.conversationSelected(conversation.id)
                        },
                        colors = colors
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState(colors: TalaColors) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = colors.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Loading conversations...",
                color = colors.secondaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    colors: TalaColors
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = colors.errorText,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "Something went wrong",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )

                Text(
                    text = error,
                    fontSize = 14.sp,
                    color = colors.secondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.primaryButtonText
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Retry",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.secondaryButtonBackground,
                            contentColor = colors.secondaryButtonText
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Dismiss",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(colors: TalaColors) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                tint = colors.secondaryText.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "No conversations yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
            )

            Text(
                text = "Start your first conversation to see it here",
                fontSize = 14.sp,
                color = colors.secondaryText.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ConversationList(
    conversations: List<Conversation>,
    onConversationClick: (Conversation) -> Unit,
    colors: TalaColors
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(
            items = conversations,
            key = { it.id }
        ) { conversation ->
            ConversationItem(
                conversation = conversation,
                onClick = { onConversationClick(conversation) },
                colors = colors
            )
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit,
    colors: TalaColors
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = conversation.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!conversation.topic.isNullOrBlank()) {
                        Text(
                            text = conversation.topic,
                            fontSize = 14.sp,
                            color = colors.secondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp),
                            lineHeight = 18.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = conversation.language,
                        fontSize = 12.sp,
                        color = colors.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConversationStat(
                        icon = Icons.Default.Chat,
                        value = "${conversation.totalMessages}",
                        colors = colors
                    )

                    ConversationStat(
                        icon = Icons.Default.Schedule,
                        value = formatDuration(conversation.sessionDuration),
                        colors = colors
                    )
                }

                Text(
                    text = formatTimestamp(conversation.updatedAt),
                    fontSize = 12.sp,
                    color = colors.secondaryText,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ConversationStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    colors: TalaColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = colors.primaryText,
            lineHeight = 16.sp
        )
    }
}

@OptIn(ExperimentalTime::class)
fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val currentInstant = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
    val currentDate = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    val targetDate = dateTime.date
    val oneDayMillis = 86_400_000L // 24 hours in milliseconds

    return when {
        targetDate == currentDate -> "Today"
        targetDate == currentDate.minus(1, kotlinx.datetime.DateTimeUnit.DAY) -> "Yesterday"
        else -> "${dateTime.monthNumber}/${dateTime.dayOfMonth}/${dateTime.year}"
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = durationMs / 60000
    return if (minutes < 60) {
        "${minutes}m"
    } else {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        "${hours}h ${remainingMinutes}m"
    }
}