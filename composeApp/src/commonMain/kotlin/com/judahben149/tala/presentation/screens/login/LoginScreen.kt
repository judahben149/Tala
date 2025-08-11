package com.judahben149.tala.presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.models.authentication.errors.InvalidCredentialsError
import com.judahben149.tala.domain.models.authentication.errors.NetworkError
import com.judahben149.tala.domain.models.authentication.errors.UserNotFoundError
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.navigation.components.LoginScreenComponent
import com.judahben149.tala.presentation.UiState
import com.judahben149.tala.presentation.components.TextFieldHint
import com.judahben149.tala.ui.theme.Red400
import com.judahben149.tala.ui.theme.Yellow400
import com.judahben149.tala.ui.theme.or
import com.judahben149.tala.util.isIos
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    component: LoginScreenComponent,
) {
    val viewModel: AuthViewModel = koinViewModel()
    val signInState by viewModel.signInState.collectAsState()

    LogInScreenContent(
        signInState = signInState,
        onSignInClick = { email, password ->
            viewModel.signIn(email, password)
        },
        onSignInWithGoogleClick = {
//            component.navigateToGoogleSignIn()
        },
        onSignInWithAppleClick = {
//            component.navigateToAppleSignIn()
        },
        onNavigateToSignUp = {
            component.navigateToSignUp()
        },
        onSignInSuccess = {
            viewModel.clearSignInState()
            component.navigateToHome()
        }
    )
}

@Composable
fun LogInScreenContent(
    signInState: UiState<AppUser, FirebaseError>?,
    onSignInClick: (String, String) -> Unit,
    onSignInWithGoogleClick: () -> Unit,
    onSignInWithAppleClick: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onSignInSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Handle sign in success
    LaunchedEffect(signInState) {
        if (signInState is UiState.Loaded && signInState.result is Result.Success) {
            onSignInSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign In")

        // Email text field
        TextFieldHint(
            hint = "Email",
            value = email,
            onValueChange = { email = it }
        )

        // Password text field
        TextFieldHint(
            hint = "Password",
            value = password,
            onValueChange = { password = it }
        )

        // Sign in button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
            onClick = { onSignInClick(email, password) },
            enabled = signInState !is UiState.Loading
        ) {
            if (signInState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text("Sign In")
            }
        }

        // Sign in with Google button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
            onClick = onSignInWithGoogleClick
        ) {
            Text("Sign in with Google")
        }

        if (isIos()) {
            // Sign in with Apple button
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
                onClick = onSignInWithAppleClick
            ) {
                Text("Sign in with Apple")
            }
        }

        // Navigate to Sign Up
        TextButton(onClick = onNavigateToSignUp) {
            Text("Don't have an account? Sign Up")
        }

        // Error handling
        signInState?.let { state ->
            if (state is UiState.Loaded && state.result is Result.Failure) {
                val errorMessage = when (state.result.error) {
                    is InvalidCredentialsError -> "Invalid email or password"
                    is NetworkError -> "Check your internet connection"
                    is UserNotFoundError -> "No account found with this email"
                    else -> "Sign in failed. Please try again"
                }

                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Red400.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = Red400
                    )
                }
            }
        }
    }
}
