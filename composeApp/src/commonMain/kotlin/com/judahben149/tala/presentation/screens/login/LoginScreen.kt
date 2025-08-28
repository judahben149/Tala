package com.judahben149.tala.presentation.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.navigation.components.others.LoginScreenComponent
import com.judahben149.tala.presentation.UiState
import com.judahben149.tala.presentation.screens.signUp.ErrorCard
import com.judahben149.tala.ui.theme.TalaColors
import com.judahben149.tala.ui.theme.getTalaColors
import com.judahben149.tala.util.isIos
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    component: LoginScreenComponent,
) {
    val viewModel: AuthViewModel = koinViewModel()
    val signInState by viewModel.signInState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val colors = getTalaColors()
    val sessionManager: SessionManager = koinInject()

    // Handle sign in success
    LaunchedEffect(signInState) {
        when (val currentState = signInState) {
            is UiState.Loaded -> {
                when (val result = currentState.result) {
                    is Result.Success -> {
                        val user = result.data
                        viewModel.clearFormState()

                        // Check if user actually completed onboarding
                        val hasCompletedOnboarding = sessionManager.checkUserOnboardingStatus(user.userId)

                        if (hasCompletedOnboarding) {
                            sessionManager.markSignedIn(
                                userId = user.userId,
                                isNewUser = false
                            )
                            component.handleLoginSuccess()
                        } else {
                            sessionManager.markSignedIn(
                                userId = user.userId,
                                isNewUser = false
                            )
                        }
                    }
                    is Result.Failure -> {
                        // Handle error if needed
                    }
                }
            }
            is UiState.Loading -> { /* Handle loading */ }
            null -> {  }
        }
    }


    LoginScreenContent(
        formState = formState,
        signInState = signInState,
        colors = colors,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onSignInClick = { viewModel.signIn() },
        onSignInWithGoogleClick = { /* Handle Google sign in */ },
        onSignInWithAppleClick = { /* Handle Apple sign in */ },
        onNavigateToSignUp = { component.navigateToSignUp() }
    )
}

@Composable
private fun LoginScreenContent(
    formState: LoginFormState,
    signInState: UiState<AppUser, FirebaseAuthException>?,
    colors: TalaColors,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onSignInWithGoogleClick: () -> Unit,
    onSignInWithAppleClick: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.iconTint,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* Handle back navigation */ }
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colors.iconTint,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* Handle close */ }
                )
            }

            // Title
            Text(
                text = "Log in",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Login Form
            LoginForm(
                formState = formState,
                colors = colors,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = onSignInClick,
                enabled = signInState !is UiState.Loading && formState.canSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primaryButtonBackground,
                    contentColor = colors.primaryButtonText,
                    disabledContainerColor = colors.disabledButtonBackground,
                    disabledContentColor = colors.disabledButtonText
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (signInState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.primaryButtonText,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Log In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Login Buttons
            SocialLoginButtons(
                colors = colors,
                onGoogleClick = onSignInWithGoogleClick,
                onAppleClick = onSignInWithAppleClick
            )

            // Navigate to Sign Up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = colors.secondaryText,
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign Up",
                    color = colors.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }

            // Error handling
            signInState?.let { state ->
                if (state is UiState.Loaded && state.result is Result.Failure) {
                    ErrorCard(
                        error = state.result.error,
                        colors = colors,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            // Form validation errors
            if (formState.showInvalidEmailError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.textFieldErrorBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Please enter a valid email address",
                        modifier = Modifier.padding(16.dp),
                        color = colors.errorText,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LoginForm(
    formState: LoginFormState,
    colors: TalaColors,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Email Field
        OutlinedTextField(
            value = formState.email,
            onValueChange = onEmailChange,
            label = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
            isError = formState.showInvalidEmailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.textFieldFocusedIndicator,
                unfocusedBorderColor = colors.textFieldBorder,
                focusedTextColor = colors.primaryText,
                unfocusedTextColor = colors.primaryText,
                unfocusedContainerColor = colors.textFieldBackground,
                focusedContainerColor = colors.textFieldBackground,
                unfocusedLabelColor = colors.secondaryText,
                focusedLabelColor = colors.textFieldFocusedIndicator,
                unfocusedPlaceholderColor = colors.textFieldPlaceholderText,
                focusedPlaceholderColor = colors.textFieldPlaceholderText,
                errorBorderColor = colors.errorText,
                errorLabelColor = colors.errorText
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Password Field
        OutlinedTextField(
            value = formState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = colors.iconTint
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.textFieldFocusedIndicator,
                unfocusedBorderColor = colors.textFieldBorder,
                focusedTextColor = colors.primaryText,
                unfocusedTextColor = colors.primaryText,
                unfocusedContainerColor = colors.textFieldBackground,
                focusedContainerColor = colors.textFieldBackground,
                unfocusedLabelColor = colors.secondaryText,
                focusedLabelColor = colors.textFieldFocusedIndicator,
                unfocusedPlaceholderColor = colors.textFieldPlaceholderText,
                focusedPlaceholderColor = colors.textFieldPlaceholderText,
                focusedTrailingIconColor = colors.iconTint,
                unfocusedTrailingIconColor = colors.iconTint
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun SocialLoginButtons(
    colors: TalaColors,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Google Log in Button
        OutlinedButton(
            onClick = onGoogleClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.primaryText
            ),
            border = BorderStroke(
                width = 1.dp,
                color = colors.textFieldBorder
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Continue with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (isIos()) {
            // Apple Sign Up Button
            OutlinedButton(
                onClick = onAppleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.primaryText
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = colors.textFieldBorder
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Continue with Apple",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
