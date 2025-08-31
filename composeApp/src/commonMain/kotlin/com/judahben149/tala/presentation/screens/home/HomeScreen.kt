package com.judahben149.tala.presentation.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.navigation.components.top.HomeScreenComponent
import com.judahben149.tala.ui.theme.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun HomeScreen(
    component: HomeScreenComponent,
    viewModel: HomeScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = getTalaColors()

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Section with greeting and profile
        TopSection(
            userName = uiState.userName,
            streakDays = uiState.streakDays,
            colors = colors,
            onProfileClick = { component.navigateToProfile() },
            onSettingsClick = { component.navigateToSettings() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Action Cards
        QuickActionsSection(
            colors = colors,
            onSpeakClick = {
                if (viewModel.isVoiceSelectionComplete()) {
                    component.navigateToSpeak()
                } else {
                    component.navigateToVoices()
                }
            },
            onVoicesClick = { component.navigateToVoices() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Learning Progress Section
        LearningProgressSection(
            learningLanguage = uiState.learningLanguage,
            totalConversations = uiState.totalConversations,
            weeklyGoalProgress = uiState.weeklyGoalProgress,
            colors = colors
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Recent Activity
        RecentActivitySection(
            recentTopics = uiState.recentTopics,
            colors = colors
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TopSection(
    userName: String,
    streakDays: Int,
    colors: TalaColors,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Greeting Section
        Column {
            Text(
                text = getGreeting(),
                fontSize = 16.sp,
                color = colors.secondaryText
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = userName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )
                if (streakDays > 0) {
                    StreakBadge(streakDays = streakDays, colors = colors)
                }
            }
        }

        // Profile Avatar and Settings
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = colors.iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                    color = colors.primaryButtonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StreakBadge(
    streakDays: Int,
    colors: TalaColors
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.cardBackground,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = colors.accentText,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "$streakDays",
                color = colors.accentText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    colors: TalaColors,
    onSpeakClick: () -> Unit,
    onVoicesClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Quick Actions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Start Conversation Card
            QuickActionCard(
                title = "Start Conversation",
                subtitle = "Practice speaking",
                icon = Icons.Default.Chat,
                backgroundColor = colors.primary,
                contentColor = colors.primaryButtonText,
                modifier = Modifier.weight(1f),
                onClick = onSpeakClick
            )

            // Change Voice Card
            QuickActionCard(
                title = "AI Voices",
                subtitle = "Choose your tutor",
                icon = Icons.Default.RecordVoiceOver,
                backgroundColor = colors.cardBackground,
                contentColor = colors.primaryText,
                modifier = Modifier.weight(1f),
                onClick = onVoicesClick
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun LearningProgressSection(
    learningLanguage: String,
    totalConversations: Int,
    weeklyGoalProgress: Float,
    colors: TalaColors
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Learning Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = learningLanguage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressItem(
                    title = "Total Chats",
                    value = "$totalConversations",
                    colors = colors
                )

                ProgressItem(
                    title = "This Week",
                    value = "${(weeklyGoalProgress * 100).toInt()}%",
                    colors = colors
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weekly Progress Bar
            Column {
                Text(
                    text = "Weekly Goal Progress",
                    fontSize = 12.sp,
                    color = colors.secondaryText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LinearProgressIndicator(
                    progress = { weeklyGoalProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colors.primary,
                    trackColor = colors.primary.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun ProgressItem(
    title: String,
    value: String,
    colors: TalaColors
) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            color = colors.secondaryText
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText
        )
    }
}

@Composable
fun RecentActivitySection(
    recentTopics: List<String>,
    colors: TalaColors
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Recent Topics",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (recentTopics.isEmpty()) {
            EmptyState(colors = colors)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(recentTopics) { topic ->
                    TopicChip(topic = topic, colors = colors)
                }
            }
        }
    }
}

@Composable
private fun TopicChip(
    topic: String,
    colors: TalaColors
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.primary.copy(alpha = 0.1f)
    ) {
        Text(
            text = topic,
            fontSize = 14.sp,
            color = colors.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun EmptyState(colors: TalaColors) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            tint = colors.secondaryText.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No recent conversations yet",
            fontSize = 14.sp,
            color = colors.secondaryText,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Start your first conversation to see topics here",
            fontSize = 12.sp,
            color = colors.secondaryText.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun getGreeting(): String {
    val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    return when (hour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}
