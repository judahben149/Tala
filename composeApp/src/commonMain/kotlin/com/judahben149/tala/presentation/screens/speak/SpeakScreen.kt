package com.judahben149.tala.presentation.screens.speak

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.navigation.components.others.SpeakScreenComponent
import org.koin.compose.viewmodel.koinViewModel

/**
 * Simple verification screen:
 * - Record (WAV)
 * - Stop to finalize WAV
 * - Play to verify it’s playable locally
 */
@Composable
fun SpeakScreen(
    speakScreenComponent: SpeakScreenComponent
) {
    val viewModel: SpeakScreenViewModel = koinViewModel()

    val recordingStatus by viewModel.recordingStatus.collectAsStateWithLifecycle()
    val isRecordingLoading by viewModel.isRecordingLoading.collectAsStateWithLifecycle()
    val recordingError by viewModel.recordingError.collectAsStateWithLifecycle()
    val recordedBytes by viewModel.recordedAudioBytes.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    val isRecording = recordingStatus == com.judahben149.tala.domain.models.speech.RecorderStatus.Recording
    val canStart = recordingStatus == com.judahben149.tala.domain.models.speech.RecorderStatus.Idle && !isRecordingLoading
    val canStop = recordingStatus == com.judahben149.tala.domain.models.speech.RecorderStatus.Recording && !isRecordingLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isRecording)
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "WAV Capture & Playback",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Status: ${recordingStatus.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            when {
                                canStart -> viewModel.startRecording()
                                canStop -> viewModel.stopRecording()
                            }
                        },
                        enabled = canStart || canStop,
                        colors = if (isRecording)
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        else ButtonDefaults.buttonColors(),
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        if (isRecordingLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.KeyboardVoice,
                                contentDescription = if (isRecording) "Stop Recording" else "Start Recording"
                            )
                        }
                    }

                    if (isRecording) {
                        OutlinedButton(onClick = { viewModel.cancelRecording() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Cancel")
                            Spacer(modifier = Modifier.size(4.dp))
                            Text("Cancel")
                        }
                    }
                }

                recordingError?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(onClick = { /* dismiss only */ }) {
                                Text("Dismiss")
                            }
                        }
                    }
                }

                // Playback controls for the recorded WAV
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.playRecorded() },
                        enabled = recordedBytes != null && !isRecording && !isPlaying
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Play")
                    }

                    OutlinedButton(
                        onClick = { viewModel.stopPlayback() },
                        enabled = isPlaying
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop")
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Stop")
                    }

                    OutlinedButton(
                        onClick = { viewModel.clearRecording() },
                        enabled = recordedBytes != null && !isRecording && !isPlaying
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Clear")
                    }
                }

                if (recordedBytes != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recorded WAV size: ${recordedBytes!!.size} bytes",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
