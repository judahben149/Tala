package com.judahben149.tala.presentation.screens.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.judahben149.tala.domain.usecases.authentication.CreateUserUseCase

class SignUpViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AppUser, FirebaseError>?>(null)
    val uiState: StateFlow<UiState<AppUser, FirebaseError>?> = _uiState.asStateFlow()

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

    // Keep this for backward compatibility with existing screen code
    fun updateDisplayName(displayName: String) {
        _formState.value = _formState.value.copy(firstName = displayName)
    }

    fun signUp() {
        val currentState = _formState.value

        // Validate form before proceeding
        if (!currentState.isValid()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // Combine first and last name for display name
            val fullDisplayName = "${currentState.firstName} ${currentState.lastName}".trim()

            val result = createUserUseCase(
                email = currentState.email,
                password = currentState.password,
                displayName = fullDisplayName.ifEmpty { currentState.firstName }
            )

            _uiState.value = UiState.Loaded(result)
        }
    }

    fun clearState() {
        _uiState.value = null
        _formState.value = SignUpFormState()
    }
}

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

    // Helper properties for validation states
    val showEmailMismatchError: Boolean
        get() = confirmEmail.isNotBlank() && !emailsMatch()

    val showInvalidEmailError: Boolean
        get() = email.isNotBlank() && !isValidEmail(email)

    val showWeakPasswordError: Boolean
        get() = password.isNotBlank() && !hasValidPassword()
}
