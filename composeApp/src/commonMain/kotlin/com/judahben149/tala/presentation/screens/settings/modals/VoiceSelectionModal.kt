package com.judahben149.tala.presentation.screens.settings.modals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.ui.theme.TalaColors

@Composable
fun VoiceSelectionModal(
    voices: List<SimpleVoice>,
    selectedVoiceId: String?,
    isPlayingSample: Boolean,
    playingVoiceId: String?,
    onVoiceSelected: (String) -> Unit,
    onPlayVoiceSample: (String) -> Unit,
    onDismissRequest: () -> Unit,
    colors: TalaColors
) {

    val listState = rememberLazyListState()
    val selectedIndex = voices.indexOfFirst { it.voiceId == selectedVoiceId }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            listState.scrollToItem(index = selectedIndex)
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Select Voice",
                color = colors.primaryText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(voices) { voice ->
                    VoiceItem(
                        voice = voice,
                        isSelected = voice.voiceId == selectedVoiceId,
                        isPlaying = isPlayingSample && playingVoiceId == voice.voiceId,
                        onVoiceSelected = { onVoiceSelected(voice.voiceId) },
                        onPlayVoiceSample = { onPlayVoiceSample(voice.voiceId) },
                        colors = colors
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = "Close",
                    color = colors.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = colors.cardBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun VoiceItem(
    voice: SimpleVoice,
    isSelected: Boolean,
    isPlaying: Boolean,
    onVoiceSelected: () -> Unit,
    onPlayVoiceSample: () -> Unit,
    colors: TalaColors
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVoiceSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colors.primary.copy(alpha = 0.1f) else Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onVoiceSelected() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.primary,
                    unselectedColor = colors.secondaryText
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = voice.name,
                    color = if (isSelected) colors.primary else colors.primaryText,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            // Play button with loading state
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = { onPlayVoiceSample() },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Voice Sample",
                            tint = colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
