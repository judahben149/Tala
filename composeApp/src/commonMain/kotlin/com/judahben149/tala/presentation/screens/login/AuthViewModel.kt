package com.judahben149.tala.presentation.screens.login

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
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.authentication.GetUserDataUseCase
import com.judahben149.tala.domain.usecases.authentication.IsUserSignedInUseCase
import com.judahben149.tala.domain.usecases.authentication.SendPasswordResetEmailUseCase
import com.judahben149.tala.domain.usecases.authentication.SignInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.preferences.SaveLearningLanguageUseCase
import com.judahben149.tala.domain.usecases.user.PersistUserDataUseCase
import com.judahben149.tala.presentation.UiState
import com.judahben149.tala.util.buildAppUserFromProfileData
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val persistUserDataUseCase: PersistUserDataUseCase,
    private val createDefaultUserDataUseCase: CreateDefaultUserDataUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isUserSignedInUseCase: IsUserSignedInUseCase,
    private val saveLearningLanguageUseCase: SaveLearningLanguageUseCase,
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

    fun handleFederatedSignUp(
        user: FirebaseUser,
        signInMethod: SignInMethod,
        signUpCompleted:(userId: String, isNewUser: Boolean) -> Unit,
        signUpFailed:(errorMessage: String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val userData = getUserDataUseCase(user.uid)

            when (userData) {
                is Result.Success -> {
                    if (userData.data.isEmpty()) {
                        when(val result = createDefaultUserDataUseCase(user.toAppUser().copy(signInMethod = signInMethod), true)) {
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
                        sessionManager.updateOnboardingFlag(true)
                        logger.d { "User data already exists: ${userData.data}" }

                        val data = buildAppUserFromProfileData(user.uid, userData.data)
                        logger.d { "User data: $data" }
                        persistUserDataUseCase(data)
                        signUpCompleted(user.uid, false)
                    }
                }
                is Result.Failure -> {
                    if (userData.error.message == "User data not found") {
                        when(val result = createDefaultUserDataUseCase(user.toAppUser(), true)) {
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
        }
    }

    fun handleEmailSignup(
        user: AppUser,
        signUpCompleted:(userId: String, isNewUser: Boolean) -> Unit,
        signUpFailed:(errorMessage: String) -> Unit
    ) {

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val userData = getUserDataUseCase(user.userId)

            when (userData) {
                is Result.Success -> {
                    if (userData.data.isEmpty()) {
                        when(val result = createDefaultUserDataUseCase(user, true)) {
                            is Result.Success -> {
                                logger.d { "User data created successfully: ${result.data}" }
                                signUpCompleted(user.userId, true)
                            }
                            is Result.Failure -> {
                                logger.e { "User data creation failed: ${result.error}" }
                                signUpFailed(result.error.message ?: "Unknown error")
                            }
                        }
                    } else {
                        sessionManager.updateOnboardingFlag(true)
                        logger.d { "User data already exists: ${userData.data}" }

                        val data = buildAppUserFromProfileData(user.userId, userData.data)
                        logger.d { "User data: $data" }
                        persistUserDataUseCase(data)
                        saveLearningLanguageUseCase(data.learningLanguage)
                        logger.d { "Learning language saved: ${data.learningLanguage}" }
                        signUpCompleted(user.userId, false)
                    }
                }
                is Result.Failure -> {
                    if (userData.error.message == "User data not found") {
                        when(val result = createDefaultUserDataUseCase(user, true)) {
                            is Result.Success -> {
                                logger.d { "User data created successfully: ${result.data}" }
                                signUpCompleted(user.userId, true)
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
        }


    }


    fun logStuff(any: Any) {
        logger.d { "Stuff: $any" }
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
