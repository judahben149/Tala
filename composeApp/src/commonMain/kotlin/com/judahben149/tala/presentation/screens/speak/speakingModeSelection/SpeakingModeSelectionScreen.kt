package com.judahben149.tala.presentation.screens.speak.speakingModeSelection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.judahben149.tala.domain.models.conversation.SpeakingMode
import com.judahben149.tala.navigation.components.others.SpeakingModeSelectionComponent
import com.judahben149.tala.presentation.shared_components.buttons.BackButton
import com.judahben149.tala.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingModeSelectionScreen(
    component: SpeakingModeSelectionComponent,
    viewModel: SpeakingModeSelectionViewModel = koinViewModel()
) {
    val colors = getTalaColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Section with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = component::goBack)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Choose Speaking Mode",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Header Section
        Text(
            text = "How would you like to practice today?",
            fontSize = 16.sp,
            color = colors.secondaryText,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Speaking Mode Cards
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Free Speak Mode Card
            SpeakingModeCard(
                title = "Free Speak",
                description = "Have an open conversation about any topic. Practice naturally without constraints.",
                icon = Icons.Default.Chat,
                colors = colors,
                onClick = { component.onModeSelected(SpeakingMode.FREE_SPEAK) }
            )

            // Guided Practice Mode Card
            SpeakingModeCard(
                title = "Guided Practice",
                description = "Practice specific scenarios like ordering food, job interviews, and more with structured guidance.",
                icon = Icons.Default.AutoAwesome,
                colors = colors,
                onClick = { component.onModeSelected(SpeakingMode.GUIDED_PRACTICE) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom tip
        Card(
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "💡 Tip: \nNew? Try guided practice. For open-ended practice, choose free speak.",
                fontSize = 12.sp,
                color = colors.secondaryText,
                modifier = Modifier.padding(20.dp),
                textAlign = TextAlign.Left
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SpeakingModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    colors: TalaColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = colors.secondaryText,
                    lineHeight = 20.sp
                )
            }
        }
    }
}