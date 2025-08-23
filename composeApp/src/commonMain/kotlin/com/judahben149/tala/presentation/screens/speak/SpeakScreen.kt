package com.judahben149.tala.presentation.screens.speak

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.navigation.components.others.SpeakScreenComponent
import com.judahben149.tala.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpeakScreen(
    component: SpeakScreenComponent,
    viewModel: SpeakScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val backgroundColor = if (isSystemInDarkTheme()) Black else White
    val textColor = if (isSystemInDarkTheme()) White else Black
    val colors = getTalaColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        TopBar(
            onCloseClick = { component.goBack() },
            textColor = textColor,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StateIndicator(
                uiState = uiState,
                textColor = textColor,
                primaryColor = colors.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            MainActionButton(
                uiState = uiState,
                onClick = viewModel::onButtonClicked,
                colors = colors
            )

            Spacer(modifier = Modifier.height(32.dp))

            ErrorDisplay(
                error = uiState.error,
                colors = colors
            )
        }

        BottomControls(
            uiState = uiState,
            onCancelClick = viewModel::cancelRecording,
            textColor = textColor,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TopBar(
    onCloseClick: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun StateIndicator(
    uiState: SpeakScreenUiState,
    textColor: Color,
    primaryColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = uiState.buttonIcon,
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = uiState.buttonLabel,
            color = textColor,
            fontSize = 24.sp,
            fontStyle = latoTypography().bodySmall.fontStyle,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.buttonAction,
            color = primaryColor,
            fontSize = 16.sp,
            fontStyle = latoTypography().bodySmall.fontStyle,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MainActionButton(
    uiState: SpeakScreenUiState,
    onClick: () -> Unit,
    colors: TalaColors
) {
    Button(
        onClick = onClick,
        enabled = uiState.isButtonEnabled,
        modifier = Modifier.size(120.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primaryButtonBackground,
            contentColor = colors.primaryButtonText,
            disabledContainerColor = colors.disabledButtonBackground,
            disabledContentColor = colors.disabledButtonText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = colors.primaryButtonText,
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                imageVector = uiState.buttonIcon,
                contentDescription = uiState.buttonLabel,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun ErrorDisplay(
    error: String?,
    colors: TalaColors
) {
    error?.let { errorMessage ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.textFieldErrorBackground
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = errorMessage,
                color = colors.errorText,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                fontStyle = latoTypography().bodySmall.fontStyle
            )
        }
    }
}

@Composable
private fun BottomControls(
    uiState: SpeakScreenUiState,
    onCancelClick: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.conversationState == ConversationState.Recording) {
            TextButton(onClick = onCancelClick) {
                Text(
                    text = "Cancel",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = latoTypography().bodySmall.fontStyle
                )
            }
        }

        if (uiState.canInterrupt) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap to interrupt and speak again",
                color = textColor.copy(alpha = 0.6f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 32.dp),
                fontStyle = latoTypography().bodySmall.fontStyle
            )
        }
    }
}
