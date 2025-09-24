package com.judahben149.tala.presentation.screens.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.mappers.toAppUser
import com.judahben149.tala.domain.models.authentication.SignInMethod
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.usecases.authentication.CreateDefaultUserDataUseCase
import com.judahben149.tala.domain.usecases.authentication.CreateUserUseCase
import com.judahben149.tala.domain.usecases.authentication.GetUserDataUseCase
import com.judahben149.tala.domain.usecases.authentication.verification.SendEmailVerificationUseCase
import com.judahben149.tala.presentation.UiState
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val createDefaultUserDataUseCase: CreateDefaultUserDataUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : ViewModel() {

    // Unified loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<AppUser, FirebaseAuthException>?>(null)
    val uiState: StateFlow<UiState<AppUser, FirebaseAuthException>?> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(SignUpFormState())
    val formState: StateFlow<SignUpFormState> = _formState.asStateFlow()

    fun updateEmail(email: String) {
        _formState.value = _formState.value.copy(email = email)
    }

    fun updateConfirmEmail(confirmEmail: String) {
        _formState.value = _formState.value.copy(confirmEmail = confirmEmail)
    }

    fun updateFirstName(firstName: String) {
        _formState.value = _formState.value.copy(firstName = firstName)
    }

    fun updateLastName(lastName: String) {
        _formState.value = _formState.value.copy(lastName = lastName)
    }

    fun updatePassword(password: String) {
        _formState.value = _formState.value.copy(password = password)
    }

    fun signUp() {
        val currentState = _formState.value
        if (!currentState.isValid()) {
            logger.w { "Form validation failed" }
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = UiState.Loading

            val fullDisplayName = "${currentState.firstName} ${currentState.lastName}".trim()
            val result = createUserUseCase(
                email = currentState.email,
                password = currentState.password,
                displayName = fullDisplayName.ifEmpty { currentState.firstName }
            )

            when (result) {
                is Result.Success -> {
                    logger.d { "User created successfully: ${result.data.email}" }
                    // Send verification email immediately after user creation
                    when (sendEmailVerificationUseCase()) {
                        is Result.Success -> {
                            logger.d { "Verification email sent successfully" }
                            _uiState.value = UiState.Loaded(result)
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to send verification email, but user was created" }
                            // Still navigate to verification screen even if email sending failed
                            // User can resend from there
                            _uiState.value = UiState.Loaded(result)
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "User creation failed: ${result.error}" }
                    _uiState.value = UiState.Loaded(result)
                }
            }
            _isLoading.value = false
        }
    }

    fun handleFederatedSignUp(
        user: FirebaseUser,
        signInMethod: SignInMethod,
        signUpCompleted: (userId: String, isNewUser: Boolean) -> Unit,
        signUpFailed: (errorMessage: String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val userData = getUserDataUseCase(user.uid)
            val appUser = user.toAppUser().copy(signInMethod = signInMethod)
            logger.d { "AppUser here: $appUser" }

            when (userData) {
                is Result.Success -> {
                    if (userData.data.isEmpty()) {
                        when (val result = createDefaultUserDataUseCase(appUser, true)) {
                            is Result.Success -> {
                                logger.d { "User data created successfully: ${result.data}" }
                                signUpCompleted(user.uid, true)
                            }
                            is Result.Failure -> {
                                logger.e { "User data creation failed: ${result.error}" }
                                signUpFailed(result.error.message ?: "Unknown error")
                            }
                        }
                    } else {
                        logger.d { "User data already exists: ${userData.data}" }
                        signUpCompleted(user.uid, false)
                    }
                }
                is Result.Failure -> {
                    if (userData.error.message == "User data not found") {
                        when (val result = createDefaultUserDataUseCase(appUser, true)) {
                            is Result.Success -> {
                                logger.d { "User data created successfully: ${result.data}" }
                                signUpCompleted(user.uid, true)
                            }
                            is Result.Failure -> {
                                logger.e { "User data creation failed: ${result.error}" }
                                signUpFailed(result.error.message ?: "Unknown error")
                            }
                        }
                    } else {
                        logger.e { "User data retrieval failed: ${userData.error}" }
                        signUpFailed(userData.error.message ?: "Unknown error")
                    }
                }
            }
            _isLoading.value = false
        }
    }

    fun triggerLoading() {
        _isLoading.value = true
    }

    fun clearState() {
        _uiState.value = null
        _formState.value = SignUpFormState()
        _isLoading.value = false
    }

    fun logStuff(any: Any) {
        logger.d { "Stuff: $any" }
    }
}


// Keep SignUpFormState unchanged
data class SignUpFormState(
    val email: String = "",
    val confirmEmail: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = ""
) {
    fun isValid(): Boolean {
        return email.isNotBlank() &&
                confirmEmail.isNotBlank() &&
                firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                password.isNotBlank() &&
                email == confirmEmail &&
                isValidEmail(email) &&
                password.length >= 6
    }

    fun emailsMatch(): Boolean {
        return email == confirmEmail
    }

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false

        val emailRegex = Regex(
            pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        return emailRegex.matches(email)
    }

    fun hasValidPassword(): Boolean {
        return password.length >= 6
    }

    val showEmailMismatchError: Boolean
        get() = confirmEmail.isNotBlank() && !emailsMatch()

    val showInvalidEmailError: Boolean
        get() = email.isNotBlank() && !isValidEmail(email)

    val showWeakPasswordError: Boolean
        get() = password.isNotBlank() && !hasValidPassword()
}
