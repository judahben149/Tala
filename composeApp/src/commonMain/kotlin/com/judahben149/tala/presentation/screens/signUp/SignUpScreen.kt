package com.judahben149.tala.presentation.screens.signUp

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
import androidx.compose.foundation.layout.width
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
import coil3.compose.AsyncImage
import com.judahben149.tala.core.purchases.associateUserWithRevenueCat
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.authentication.SignInMethod
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.navigation.components.others.SignUpScreenComponent
import com.judahben149.tala.presentation.UiState
import com.judahben149.tala.ui.theme.TalaColors
import com.judahben149.tala.ui.theme.getTalaColors
import com.judahben149.tala.util.isIos
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.models.CustomerInfo
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import tala.composeapp.generated.resources.Res
import tala.composeapp.generated.resources.google_logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    component: SignUpScreenComponent,
) {
    val viewModel: SignUpViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colors = getTalaColors()
    val sessionManager: SessionManager = koinInject()


    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is UiState.Loaded -> {
                when (val result = currentState.result) {
                    is Result.Success -> {
                        val user = result.data
                        // Navigate to email verification screen instead of main app
                        component.navigateToEmailVerification(user.email)
                        viewModel.clearState()
                    }
                    is Result.Failure -> {
                        // Handle error - error is displayed in UI already
                    }
                }
            }
            is UiState.Loading -> { /* Handle loading */ }
            null -> { }
        }
    }

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
                        .clickable { component.navigateToLogin() }
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colors.iconTint,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { component.navigateToLogin() }
                )
            }

            // Title
            Text(
                text = "Create an account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Form Fields
            SignUpForm(
                formState = formState,
                colors = colors,
                onEmailChange = viewModel::updateEmail,
                onConfirmEmailChange = viewModel::updateConfirmEmail,
                onPasswordChange = viewModel::updatePassword,
                onFirstNameChange = viewModel::updateFirstName,
                onLastNameChange = viewModel::updateLastName
            )

            // Terms and Privacy
            Text(
                text = "By signing up or signing in, I accept the Tala Terms of Service and have read the Privacy Policy.",
                fontSize = 12.sp,
                color = colors.secondaryText,
                lineHeight = 16.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Sign Up Button
            Button(
                onClick = { viewModel.signUp() },
                enabled = !isLoading && formState.isValid(),
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
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.primaryButtonText,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            GoogleButtonUiContainerFirebase(
                linkAccount = false,
                filterByAuthorizedAccounts = true,
                onResult = { result ->
                    result.fold(
                        onSuccess = { firebaseUser ->
                            firebaseUser?.let { user ->
                                viewModel.logStuff(user.displayName.toString())

//                                associateUserWithRevenueCat(
//                                    userId = user.uid,
//                                    onUserAssociated = { customerInfo, created ->
//                                        viewModel.logStuff(customerInfo.toString())

                                        viewModel.handleFederatedSignUp(
                                            user = user,
                                            signInMethod = SignInMethod.GOOGLE,
                                            signUpCompleted = { userId, isNewUser ->
                                                sessionManager.markSignedIn(
                                                    userId = user.uid,
                                                    isNewUser = isNewUser
                                                )
                                                component.handleSignUpSuccess()
                                            },
                                            signUpFailed = { errorMessage ->
                                                viewModel.logStuff("Error yoo$errorMessage")
                                            }
                                        )
//                                    },
//                                    onUserAssociationFailed = {
//                                        viewModel.logStuff("User association failed")
//                                    }
//                                )
                            }
                        },
                        onFailure = { error ->
                            viewModel.logStuff(error.toString())
                        }
                    )
                }
            ) {

                // Google Sign Up Button
                OutlinedButton(
                    onClick = { this@GoogleButtonUiContainerFirebase.onClick() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.primaryText,
                        disabledContentColor = colors.disabledButtonText
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = colors.textFieldBorder
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = Res.getUri("drawable/google_logo.png"),
                        contentDescription = "Google logo",
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign in with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (isIos()) {
                Spacer(modifier = Modifier.height(24.dp))

                AppleButtonUiContainer(
                    linkAccount = false,
                    onResult = { result ->
                        result.fold(
                            onSuccess = { firebaseUser ->
                                firebaseUser?.let { user ->
                                    viewModel.logStuff(user.displayName.toString())

//                                    associateUserWithRevenueCat(
//                                        userId = user.uid,
//                                        onUserAssociated = { customerInfo, created ->
//                                            viewModel.logStuff(customerInfo.toString())
//
//                                            viewModel.handleFederatedSignUp(
//                                                user = user,
//                                                signInMethod = SignInMethod.APPLE,
//                                                signUpCompleted = { userId, isNewUser ->
//
//                                                    if (isNewUser) {
//                                                        sessionManager.markSignedIn(
//                                                            userId = user.uid,
//                                                            isNewUser = true
//                                                        )
//                                                        component.handleSignUpSuccess()
//                                                    } else {
//                                                        sessionManager.markSignedIn(
//                                                            userId = user.uid,
//                                                            isNewUser = false
//                                                        )
//                                                        component.handleSignUpSuccess()
//                                                    }
//                                                },
//                                                signUpFailed = { errorMessage ->
//                                                    viewModel.logStuff("Error yoo$errorMessage")
//                                                }
//                                            )
//                                        },
//                                        onUserAssociationFailed = {
//                                            viewModel.logStuff("User association failed")
//                                        }
//                                    )


                                    viewModel.handleFederatedSignUp(
                                        user = user,
                                        signInMethod = SignInMethod.APPLE,
                                        signUpCompleted = { userId, isNewUser ->

                                            if (isNewUser) {
                                                sessionManager.markSignedIn(
                                                    userId = user.uid,
                                                    isNewUser = true
                                                )
                                                component.handleSignUpSuccess()
                                            } else {
                                                sessionManager.markSignedIn(
                                                    userId = user.uid,
                                                    isNewUser = false
                                                )
                                                component.handleSignUpSuccess()
                                            }
                                        },
                                        signUpFailed = { errorMessage ->
                                            viewModel.logStuff("Error yoo$errorMessage")
                                        }
                                    )
                                }
                            },
                            onFailure = { error ->
                                viewModel.logStuff(error.toString())
                            }
                        )
                    }
                ) {

                    // Apple Sign Up Button
                    OutlinedButton(
                        onClick = { this@AppleButtonUiContainer.onClick() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primaryText,
                            disabledContentColor = colors.disabledButtonText
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = colors.textFieldBorder
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        AsyncImage(
                            model = Res.getUri("drawable/apple_logo.png"),
                            contentDescription = "Apple logo",
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign in with Apple",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Social Sign Up Buttons
//            SocialSignUpButtons(
//                    Text(
//                        text = "Continue with Google",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            }

            // Navigate to Sign In
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    color = colors.secondaryText,
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign In",
                    color = colors.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { component.navigateToLogin() }
                )
            }

            // Error handling
            uiState?.let { state ->
                if (state is UiState.Loaded && state.result is Result.Failure) {
                    ErrorCard(
                        error = state.result.error,
                        colors = colors,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
private fun SignUpForm(
    formState: SignUpFormState,
    colors: TalaColors,
    onEmailChange: (String) -> Unit,
    onConfirmEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Email Field
        OutlinedTextField(
            value = formState.email,
            onValueChange = onEmailChange,
            label = { Text("Email address", color = colors.secondaryText) },
            placeholder = { Text("alexandrh.mobbin@gmail.com", color = colors.textFieldPlaceholderText) },
            modifier = Modifier.fillMaxWidth(),
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
                focusedPlaceholderColor = colors.textFieldPlaceholderText
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Confirm Email Field
        OutlinedTextField(
            value = formState.confirmEmail,
            onValueChange = onConfirmEmailChange,
            label = { Text("Confirm email address", color = colors.secondaryText) },
            modifier = Modifier.fillMaxWidth(),
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
                focusedPlaceholderColor = colors.textFieldPlaceholderText
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // First Name Field
        OutlinedTextField(
            value = formState.firstName,
            onValueChange = onFirstNameChange,
            label = { Text("First name", color = colors.secondaryText) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
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
                focusedPlaceholderColor = colors.textFieldPlaceholderText
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Last Name Field
        OutlinedTextField(
            value = formState.lastName,
            onValueChange = onLastNameChange,
            label = { Text("Last name", color = colors.secondaryText) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
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
                focusedPlaceholderColor = colors.textFieldPlaceholderText
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Password Field
        OutlinedTextField(
            value = formState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password", color = colors.secondaryText) },
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
                focusedPlaceholderColor = colors.textFieldPlaceholderText
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun SocialSignUpButtons(
    colors: TalaColors,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Google Sign Up Button
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

@Composable
fun ErrorCard(
    error: FirebaseAuthException,
    colors: TalaColors,
    modifier: Modifier = Modifier
) {
    val errorMessage = error.message

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.textFieldErrorBackground
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = errorMessage ?: "An error occurred",
            modifier = Modifier.padding(16.dp),
            color = colors.errorText,
            fontSize = 14.sp
        )
    }
}