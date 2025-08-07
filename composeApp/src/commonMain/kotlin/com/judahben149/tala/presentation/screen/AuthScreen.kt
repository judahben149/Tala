package com.judahben149.tala.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.judahben149.tala.presentation.navigation.AuthComponent

@Composable
fun AuthScreen(
    component: AuthComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Tala",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { component.onSignInWithGoogle() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { component.onSignInWithApple() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Apple")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Test Screens",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { component.onNavigateToRoomTest() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Room Test")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedButton(
            onClick = { component.onNavigateToPrefsTest() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Preferences Test")
        }
        
        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
        
        state.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
