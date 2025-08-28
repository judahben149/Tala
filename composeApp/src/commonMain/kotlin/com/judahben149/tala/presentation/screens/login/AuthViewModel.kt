package com.judahben149.tala.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.authentication.IsUserSignedInUseCase
import com.judahben149.tala.domain.usecases.authentication.SendPasswordResetEmailUseCase
import com.judahben149.tala.domain.usecases.authentication.SignInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AppUser?, FirebaseAuthException>>(UiState.Loading)
    val uiState: StateFlow<UiState<AppUser?, FirebaseAuthException>> = _uiState.asStateFlow()

    private val _signInState = MutableStateFlow<UiState<AppUser, FirebaseAuthException>?>(null)
    val signInState: StateFlow<UiState<AppUser, FirebaseAuthException>?> = _signInState.asStateFlow()

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun updateEmail(email: String) {
        _formState.value = _formState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _formState.value = _formState.value.copy(password = password)
    }

    fun signIn(email: String? = null, password: String? = null) {
        val currentState = _formState.value
        val emailToUse = email ?: currentState.email
        val passwordToUse = password ?: currentState.password

        // Validate form before proceeding
        if (emailToUse.isBlank() || passwordToUse.isBlank()) {
            return
        }

        viewModelScope.launch {
            _signInState.value = UiState.Loading

            when (val result = signInUseCase(emailToUse, passwordToUse)) {
                is Result.Success -> {
                    _signInState.value = UiState.Loaded(result)
                    checkCurrentUser() // Refresh current user state
                }
                is Result.Failure -> {
                    _signInState.value = UiState.Loaded(result)
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            when (signOutUseCase()) {
                is Result.Success -> checkCurrentUser()
                is Result.Failure -> {
                    // Handle sign out error if needed
                    checkCurrentUser()
                }
            }
        }
    }

    fun checkCurrentUser() {
        when (val result = getCurrentUserUseCase()) {
            is Result.Success -> {
                _uiState.value = UiState.Loaded(result)
            }
            is Result.Failure -> {
                _uiState.value = UiState.Loaded(result)
            }
        }
    }

    fun clearSignInState() {
        _signInState.value = null
    }

    fun clearFormState() {
        _formState.value = LoginFormState()
    }

    fun clearAllStates() {
        clearSignInState()
        clearFormState()
    }

    fun isUserSignedIn(): Boolean = isUserSignedInUseCase()
}

data class LoginFormState(
    val email: String = "",
    val password: String = ""
) {
    fun isValid(): Boolean {
        return email.isNotBlank() &&
                password.isNotBlank() &&
                isValidEmail(email)
    }

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false

        val emailRegex = Regex(
            pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        return emailRegex.matches(email)
    }

    // Helper properties for validation states
    val showInvalidEmailError: Boolean
        get() = email.isNotBlank() && !isValidEmail(email)

    val canSignIn: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}
