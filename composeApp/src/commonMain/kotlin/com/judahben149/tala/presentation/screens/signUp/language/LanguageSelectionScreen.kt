package com.judahben149.tala.presentation.screens.signUp.language

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.navigation.components.others.LanguageSelectionComponent
import com.judahben149.tala.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    component: LanguageSelectionComponent,
    viewModel: LanguageSelectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = getTalaColors()

    LaunchedEffect(Unit) {
        viewModel.loadLanguages()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {
        // Top Bar
        TopBar(
            onBackClick = { component.goBack() },
            colors = colors
        )

        // Header Section
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Your Learning Language",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Select the language you want to practice with Tala",
                fontSize = 16.sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Language List
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.availableLanguages) { language ->
                LanguageItem(
                    language = language,
                    isSelected = language == uiState.selectedLanguage,
                    colors = colors,
                    onSelect = { viewModel.selectLanguage(it) }
                )
            }
        }

        // Continue Button
        Button(
            onClick = {
                viewModel.saveSelectedLanguage()
                component.selectLanguages(listOf(uiState.selectedLanguage?.name ?: "Spanish"))
            },
            enabled = uiState.selectedLanguage != null && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primaryButtonBackground,
                contentColor = colors.primaryButtonText
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = colors.primaryButtonText,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    colors: TalaColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 24.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.primaryText,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "Step 1 of 2",
            color = colors.secondaryText,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    colors: TalaColors,
    onSelect: (Language) -> Unit
) {
    val displayNames = mapOf(
        Language.SPANISH to "🇪🇸 Spanish",
        Language.SWEDISH to "🇸🇪 Swedish",
        Language.FRENCH to "🇫🇷 French",
        Language.GERMAN to "🇩🇪 German",
        Language.ITALIAN to "🇮🇹 Italian",
        Language.JAPANESE to "🇯🇵 Japanese",
        Language.KOREAN to "🇰🇷 Korean",
        Language.MANDARIN to "🇨🇳 Mandarin",
        Language.ENGLISH to "🇺🇸 English"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect(language) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colors.primary.copy(alpha = 0.1f) else colors.cardBackground
        ),
        border = if (isSelected) BorderStroke(2.dp, colors.primary) else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayNames[language] ?: language.name,
                fontSize = 18.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) colors.primary else colors.primaryText,
                modifier = Modifier.weight(1f)
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
