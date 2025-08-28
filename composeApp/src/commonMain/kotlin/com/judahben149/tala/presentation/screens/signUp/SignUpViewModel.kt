package com.judahben149.tala.presentation.screens.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.judahben149.tala.domain.usecases.authentication.CreateUserUseCase
import com.judahben149.tala.domain.usecases.authentication.verification.SendEmailVerificationUseCase

class SignUpViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val logger: Logger
) : ViewModel() {

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
        }
    }

    fun clearState() {
        _uiState.value = null
        _formState.value = SignUpFormState()
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
