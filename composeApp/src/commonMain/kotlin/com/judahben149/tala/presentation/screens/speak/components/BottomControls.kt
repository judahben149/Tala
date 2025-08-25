package com.judahben149.tala.presentation.screens.speak.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.judahben149.tala.presentation.screens.speak.ConversationState
import com.judahben149.tala.presentation.screens.speak.SpeakScreenUiState
import com.judahben149.tala.ui.theme.latoTypography

@Composable
fun BottomControls(
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