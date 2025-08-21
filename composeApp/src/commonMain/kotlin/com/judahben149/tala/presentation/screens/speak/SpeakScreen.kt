package com.judahben149.tala.presentation.screens.speak

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.judahben149.tala.navigation.components.others.SpeakScreenComponent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpeakScreen(
    speakScreenComponent: SpeakScreenComponent
) {
    val viewModel: SpeakScreenViewModel = koinViewModel()
    val userInput by viewModel.userInput.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = userInput,
            onValueChange = { viewModel.onUserInputChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Type your message...") }
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { viewModel.sendMessage() },
            enabled = !isLoading && userInput.isNotBlank()
        ) {
            Text(if (isLoading) "Sending..." else "Send")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "AI Response:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = aiResponse,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}