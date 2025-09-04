package com.judahben149.tala.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.judahben149.tala.ui.theme.TalaTheme
import com.judahben149.tala.ui.theme.getTalaColors
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun HomeScreenPreview() {
    TalaTheme {
        HomeScreenContent(
            uiState = HomeScreenState(
                userName = "Alexandra",
                streakDays = 7,
                totalConversations = 23,
                learningLanguage = "Spanish",
                weeklyGoalProgress = 0.7f,
                recentTopics = listOf("Travel", "Food", "Culture", "Technology")
            ),
            onProfileClick = { },
            onSettingsClick = { },
            onSpeakClick = { },
            onVoicesClick = { }
        )
    }
}

@Preview
@Composable
fun HomeScreenNewUserPreview() {
    TalaTheme {
        HomeScreenContent(
            uiState = HomeScreenState(
                userName = "Friend",
                streakDays = 0,
                totalConversations = 0,
                learningLanguage = "Spanish",
                weeklyGoalProgress = 0f,
                recentTopics = emptyList()
            ),
            onProfileClick = { },
            onSettingsClick = { },
            onSpeakClick = { },
            onVoicesClick = { }
        )
    }
}

@Composable
private fun HomeScreenContent(
    uiState: HomeScreenState,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSpeakClick: () -> Unit,
    onVoicesClick: () -> Unit
) {
    val colors = getTalaColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
            .verticalScroll(rememberScrollState())
    ) {
        TopSection(
            userName = uiState.userName,
            user = uiState.user,
            streakDays = uiState.streakDays,
            colors = colors,
            onProfileClick = onProfileClick,
            onSettingsClick = onSettingsClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuickActionsSection(
            colors = colors,
            onSpeakClick = onSpeakClick,
            onVoicesClick = onVoicesClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        LearningProgressSection(
            learningLanguage = uiState.learningLanguage,
            totalConversations = uiState.totalConversations,
            weeklyGoalProgress = uiState.weeklyGoalProgress,
            colors = colors
        )

        Spacer(modifier = Modifier.height(32.dp))

        RecentActivitySection(
            recentTopics = uiState.recentTopics,
            colors = colors
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
