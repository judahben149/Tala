package com.judahben149.tala.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.judahben149.tala.navigation.components.LoginScreenComponent

@Composable
fun LoginScreen(
    component: LoginScreenComponent
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login screen")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Greetings: ${component.getGreeting()}")

        Button(onClick = { component.goBack() }) {
            Text("Go Back")
        }
    }
}