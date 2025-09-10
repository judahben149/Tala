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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.navigation.components.top.HomeScreenComponent
import com.judahben149.tala.ui.theme.TalaColors
import com.judahben149.tala.ui.theme.getTalaColors
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.floor
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun HomeScreen(
    component: HomeScreenComponent,
    viewModel: HomeScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = getTalaColors()

//    LaunchedEffect(Unit) {
//        viewModel.loadHomeData()
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Section with greeting and profile
        TopSection(
            userName = uiState.userName,
            user = uiState.user,
            streakDays = uiState.streakDays,
            colors = colors,
            onProfileClick = { component.navigateToSettings() },
            onSettingsClick = { component.navigateToSettings() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Action Cards
        QuickActionsSection(
            colors = colors,
            onSpeakClick = {
                if (viewModel.isVoiceSelectionComplete()) {
                    component.navigateToSpeakingModeSelection()
                } else {
                    component.navigateToVoices()
                }
            },
            onVoicesClick = { component.navigateToVoices() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Overview Row
        StatsOverviewSection(
            totalConversations = uiState.totalConversations,
            weeklyGoalProgress = uiState.weeklyGoalProgress,
            colors = colors
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Learning Progress Section
        LearningProgressSection(
            learningLanguage = uiState.learningLanguage,
            totalConversations = uiState.totalConversations,
            weeklyGoalProgress = uiState.weeklyGoalProgress,
            colors = colors
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Recent Activity
//        RecentActivitySection(
//            recentTopics = uiState.recentTopics,
//            colors = colors
//        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TopSection(
    userName: String,
    user: AppUser?,
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
            // Avatar with subtle gradient border
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colors.primary.copy(alpha = 0.3f),
                                colors.accentText.copy(alpha = 0.3f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(colors.cardBackground)
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                user?.avatarUrl?.let {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "User Avatar",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Text(
                        text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                        textAlign = TextAlign.Center,
                        color = colors.primaryText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
        color = colors.primary.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "$streakDays",
                color = colors.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatsOverviewSection(
    totalConversations: Int,
    weeklyGoalProgress: Float,
    colors: TalaColors
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(3) { index ->
            when (index) {
                0 -> StatCard(
                    icon = Icons.Default.Chat,
                    value = "$totalConversations",
                    label = "Total Conversations",
                    colors = colors,
                    accentColor = colors.primary
                )
                1 -> StatCard(
                    icon = Icons.Default.TrendingUp,
                    value = "${(weeklyGoalProgress * 100).toInt()}%",
                    label = "Week Progress",
                    colors = colors,
                    accentColor = colors.successText
                )
                2 -> StatCard(
                    icon = Icons.Default.Schedule,
                    value = "${floor(totalConversations * 0.4).toInt()}m",
                    label = "Time Practiced",
                    colors = colors,
                    accentColor = colors.accentText
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    colors: TalaColors,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )

            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = colors.secondaryText,
                    lineHeight = 12.sp
                )
            }
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
            // Start Conversation Card with gradient
            QuickActionCard(
                title = "Start Conversation",
                subtitle = "Practice speaking",
                icon = Icons.Default.Chat,
                isHighlighted = true,
                colors = colors,
                modifier = Modifier.weight(1f),
                onClick = onSpeakClick
            )

            // Change Voice Card
            QuickActionCard(
                title = "AI Voices",
                subtitle = "Choose your tutor",
                icon = Icons.Default.RecordVoiceOver,
                isHighlighted = false,
                colors = colors,
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
    isHighlighted: Boolean,
    colors: TalaColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isHighlighted) colors.primary else colors.cardBackground
    val contentColor = if (isHighlighted) colors.primaryButtonText else colors.primaryText
    val subtitleColor = if (isHighlighted)
        colors.primaryButtonText.copy(alpha = 0.8f) else colors.secondaryText

    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                    color = subtitleColor
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Learning Progress",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText
                    )
                }

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

            // Weekly Progress Bar with enhanced styling
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Weekly Goal Progress",
                        fontSize = 12.sp,
                        color = colors.secondaryText
                    )
                    Text(
                        text = "${(weeklyGoalProgress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
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
fun RecentActivitySection(
    recentTopics: List<String>,
    colors: TalaColors
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Topics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
            )
            if (recentTopics.isNotEmpty()) {
                Text(
                    text = "View all",
                    fontSize = 12.sp,
                    color = colors.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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