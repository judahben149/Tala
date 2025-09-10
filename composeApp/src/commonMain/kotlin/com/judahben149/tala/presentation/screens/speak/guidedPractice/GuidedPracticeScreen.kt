package com.judahben149.tala.presentation.screens.speak.guidedPractice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.judahben149.tala.navigation.components.others.GuidedPracticeScreenComponent
import com.judahben149.tala.presentation.screens.speak.guidedPractice.components.GuidedPracticeCard
import com.judahben149.tala.presentation.shared_components.buttons.BackButton
import com.judahben149.tala.ui.theme.getTalaColors
import com.judahben149.tala.util.GuidedPracticeCard
import com.judahben149.tala.util.getDisplayName
import com.judahben149.tala.util.getEmoji
import com.judahben149.tala.util.getMaxWordCount
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GuidedPracticeScreen(
    component: GuidedPracticeScreenComponent,
    viewModel: GuidedPracticeViewModel = koinViewModel()
) {
    val scenarios by viewModel.scenarios.collectAsState()
    val masteryLevel by viewModel.userMasteryLevel.collectAsState()
    val colors = getTalaColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {

        BackButton {
            component.goBack()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // User Level Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
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
                                text = "Your Level: ${masteryLevel.getDisplayName()} ${masteryLevel.getEmoji()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Responses will be limited to ${masteryLevel.getMaxWordCount()} words",
                            fontSize = 14.sp,
                            color = colors.secondaryText
                        )
                    }
                }
            }

            // Recommended Section
            viewModel.getRecommendedScenario()?.let { recommended ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Recommended for you",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GuidedPracticeCard(
                        card = GuidedPracticeCard(scenario = recommended),
                        onStartPractice = { component.scenarioSelected(recommended) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "All Practice Scenarios",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(scenarios) { card ->
                GuidedPracticeCard(
                    card = card,
                    onStartPractice = { component.scenarioSelected(card.scenario) }
                )
            }
        }
    }
}