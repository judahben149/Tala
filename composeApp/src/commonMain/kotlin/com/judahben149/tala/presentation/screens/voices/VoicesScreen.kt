package com.judahben149.tala.presentation.screens.voices

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.navigation.components.others.VoicesScreenComponent
import com.judahben149.tala.presentation.shared_components.LoadingSpinner
import com.judahben149.tala.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VoicesScreen(
    component: VoicesScreenComponent,
    viewModel: VoicesScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = getTalaColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {
        // Top bar
        TopBar(
            onCloseClick = { component.goBack() },
            colors = colors,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        when {
            uiState.isLoading -> {
                LoadingContent(
                    modifier = Modifier.align(Alignment.Center),
                    colors = colors
                )
            }
            
            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error!!,
                    onRetry = viewModel::retryLoading,
                    colors = colors,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            uiState.voices.isNotEmpty() -> {
                VoicesContent(
                    uiState = uiState,
                    onVoiceSelected = viewModel::onVoiceSelected,
                    onSaveVoice = { viewModel.saveSelectedVoice { component.voiceSelected() } },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    onCloseClick: () -> Unit,
    colors: TalaColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Cancel",
            color = colors.primaryText,
            fontSize = 16.sp,
            modifier = Modifier.clickable { onCloseClick() }
        )
        
        Text(
            text = "Choose a voice",
            color = colors.primaryText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
    colors: TalaColors
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = colors.primary,
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading voices...",
            color = colors.secondaryText,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    colors: TalaColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = colors.errorText,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = error,
            color = colors.errorText,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primaryButtonBackground,
                contentColor = colors.primaryButtonText
            )
        ) {
            Text("Retry")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VoicesContent(
    uiState: VoicesUiState,
    onVoiceSelected: (Int) -> Unit,
    onSaveVoice: () -> Unit,
    colors: TalaColors
) {
    val pagerState = rememberPagerState(pageCount = { uiState.voices.size })
    
    LaunchedEffect(pagerState.currentPage) {
        onVoiceSelected(pagerState.currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))
        
        // Voice pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp
        ) { page ->
            VoiceCard(
                voice = uiState.voices[page],
                voicePersonaNumber = page + 1,
                isSelected = page == pagerState.currentPage,
                colors = colors
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Page indicator
        PageIndicator(
            pageCount = uiState.voices.size,
            currentPage = pagerState.currentPage,
            colors = colors
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Get started button
        Button(
            onClick = onSaveVoice,
            enabled = !uiState.isPlayingSample,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primaryButtonBackground,
                contentColor = colors.primaryButtonText,
                disabledContainerColor = colors.disabledButtonBackground,
                disabledContentColor = colors.disabledButtonText
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (uiState.isPlayingSample) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = colors.primaryButtonText,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun VoiceCard(
    voice: SimpleVoice,
    voicePersonaNumber: Int,
    isSelected: Boolean,
    colors: TalaColors
) {
    val backgroundColor = if (isSelected) {
        Brush.radialGradient(
            listOf(
                colors.primary.copy(alpha = 0.2f),
                colors.primary.copy(alpha = 0.1f),
                Color.Transparent
            )
        )
    } else {
        Brush.radialGradient(
            listOf(
                colors.cardBackground.copy(alpha = 0.1f),
                Color.Transparent
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Voice avatar (placeholder circle with gradient)
//        Box(
//            modifier = Modifier
//                .size(200.dp)
//                .clip(CircleShape)
//                .background(backgroundColor),
//            contentAlignment = Alignment.Center
//        ) {
////            Text(
////                text = voice.name.firstOrNull()?.toString() ?: "?",
////                color = colors.primary,
////                fontSize = 72.sp,
////                fontWeight = FontWeight.Bold
////            )
//
////            LoadingSpinner()
//        }
        
        Spacer(modifier = Modifier.height(32.dp))

        LoadingSpinner(
            modifier = Modifier.size(200.dp),
            voicePersonaNumber = voicePersonaNumber,
        )

        Text(
            text = voice.name,
            color = colors.primaryText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = voice.description ?: "Easygoing and versatile",
            color = colors.secondaryText,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    colors: TalaColors
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isSelected) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) colors.primary 
                        else colors.primary.copy(alpha = 0.3f)
                    )
            )
        }
    }
}
