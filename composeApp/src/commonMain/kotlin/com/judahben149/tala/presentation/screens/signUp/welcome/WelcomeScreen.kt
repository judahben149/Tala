package com.judahben149.tala.presentation.screens.signUp.welcome

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.navigation.components.others.WelcomeScreenComponent
import com.judahben149.tala.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WelcomeScreen(
    component: WelcomeScreenComponent,
    viewModel: WelcomeScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = getTalaColors()

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        
        // Top section with welcome content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            
            // Welcome Icon/Animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = colors.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = colors.primary
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Welcome Message
            Text(
                text = "Welcome to Tala!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "You're all set to start your ${uiState.selectedLanguage} learning journey!",
                fontSize = 18.sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // User personalization summary
            PersonalizationSummary(
                userName = uiState.userName,
                selectedLanguage = uiState.selectedLanguage,
                selectedInterests = uiState.selectedInterests,
                colors = colors
            )
        }
        
        // Bottom section with action button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Quick tips or features preview
            QuickTips(colors = colors)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Get Started Button
            Button(
                onClick = { 
                    viewModel.completeOnboarding()
                    component.continueToNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primaryButtonBackground,
                    contentColor = colors.primaryButtonText
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Start Learning",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalizationSummary(
    userName: String,
    selectedLanguage: String,
    selectedInterests: List<String>,
    colors: TalaColors
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your Learning Profile",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primaryText
            )
            
            SummaryItem(
                icon = Icons.Default.Person,
                label = "Name",
                value = userName,
                colors = colors
            )
            
            SummaryItem(
                icon = Icons.Default.Language,
                label = "Learning",
                value = selectedLanguage,
                colors = colors
            )
            
            if (selectedInterests.isNotEmpty()) {
                SummaryItem(
                    icon = Icons.Default.Interests,
                    label = "Interests",
                    value = selectedInterests.take(2).joinToString(", ") + 
                            if (selectedInterests.size > 2) " +${selectedInterests.size - 2} more" else "",
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    icon: ImageVector,
    label: String,
    value: String,
    colors: TalaColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = colors.primary
        )
        
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = colors.secondaryText
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.primaryText
            )
        }
    }
}

@Composable
private fun QuickTips(colors: TalaColors) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "What's next?",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.primaryText,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickTipItem(
                icon = Icons.Default.Chat,
                text = "Start chatting",
                colors = colors
            )
            QuickTipItem(
                icon = Icons.Default.Mic,
                text = "Practice speaking",
                colors = colors
            )
            QuickTipItem(
                icon = Icons.Default.School,
                text = "Track progress",
                colors = colors
            )
        }
    }
}

@Composable
private fun QuickTipItem(
    icon: ImageVector,
    text: String,
    colors: TalaColors
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = colors.accentText
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = colors.secondaryText,
            textAlign = TextAlign.Center
        )
    }
}
