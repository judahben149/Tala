package com.judahben149.tala.presentation.screens.signUp

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.domain.models.authentication.errors.EmailAlreadyInUseError
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.models.authentication.errors.InvalidEmailError
import com.judahben149.tala.domain.models.authentication.errors.NetworkError
import com.judahben149.tala.domain.models.authentication.errors.WeakPasswordError
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.navigation.components.SignUpScreenComponent
import com.judahben149.tala.presentation.UiState
import com.judahben149.tala.presentation.components.TextFieldHint
import com.judahben149.tala.ui.theme.Red400
import com.judahben149.tala.ui.theme.Yellow400
import com.judahben149.tala.ui.theme.or
import com.judahben149.tala.util.isIos
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpScreen(
    component: SignUpScreenComponent,
) {

    val viewModel: SignUpViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    SignUpScreenContent(
        formState = formState,
        uiState = uiState,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onDisplayNameChange = viewModel::updateDisplayName,
        onSignUpClick = viewModel::signUp,
        onSignUpWithGoogleClick = {
//            component.navigateToGoogleSignUp()
        },
        onSignUpWithAppleClick = {
//            component.navigateToAppleSignUp()
        },
        onNavigateToSignIn = {
            component.navigateToLogin()
        },
        onSignUpSuccess = {
            viewModel.clearState()
            component.navigateToHome()
        }
    )
}

@Composable
fun SignUpScreenContent(
    formState: SignUpFormState,
    uiState: UiState<AppUser, FirebaseError>?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onSignUpWithGoogleClick: () -> Unit,
    onSignUpWithAppleClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle sign up success
    LaunchedEffect(uiState) {
        if (uiState is UiState.Loaded && uiState.result is Result.Success) {
            onSignUpSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up")

        // Display Name text field
        TextFieldHint(
            hint = "Display Name",
            value = formState.displayName,
            onValueChange = onDisplayNameChange
        )

        // Email text field
        TextFieldHint(
            hint = "Email",
            value = formState.email,
            onValueChange = onEmailChange
        )

        // Password text field
        TextFieldHint(
            hint = "Password",
            value = formState.password,
            onValueChange = onPasswordChange
        )

        // Sign up button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
            onClick = {
//                focusManager.clearFocus()
//                keyboardController?.hide()
                onSignUpClick
            },
            enabled = uiState !is UiState.Loading
        ) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text("Sign Up")
            }
        }

        // Sign up with Google button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
            onClick = onSignUpWithGoogleClick
        ) {
            Text("Sign up with Google")
        }

        if (isIos()) {
            // Sign up with Apple button
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Red400 or Yellow400),
                onClick = onSignUpWithAppleClick
            ) {
                Text("Sign up with Apple")
            }
        }

        // Navigate to Sign In
        TextButton(onClick = onNavigateToSignIn) {
            Text("Already have an account? Sign In")
        }

        // Error handling
        uiState?.let { state ->
            if (state is UiState.Loaded && state.result is Result.Failure) {
                val errorMessage = when (state.result.error) {
                    is WeakPasswordError -> "Password must be at least 6 characters"
                    is EmailAlreadyInUseError -> "This email is already registered"
                    is InvalidEmailError -> "Please enter a valid email address"
                    is NetworkError -> "Check your internet connection"
                    else -> "Sign up failed. Please try again"
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
