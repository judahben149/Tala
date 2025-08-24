package com.judahben149.tala.presentation.screens.speak.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.judahben149.tala.presentation.screens.speak.SpeakScreenUiState
import com.judahben149.tala.ui.theme.TalaColors

@Composable
fun MainActionButton(
    uiState: SpeakScreenUiState,
    onClick: () -> Unit,
    colors: TalaColors,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = uiState.isButtonEnabled,
        modifier = modifier.size(120.dp),
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