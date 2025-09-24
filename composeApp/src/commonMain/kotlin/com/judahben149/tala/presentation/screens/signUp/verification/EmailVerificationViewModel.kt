package com.judahben149.tala.presentation.screens.signUp.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.authentication.SignInMethod
import com.judahben149.tala.domain.usecases.authentication.verification.CheckEmailVerificationUseCase
import com.judahben149.tala.domain.usecases.authentication.verification.SendEmailVerificationUseCase
import kotlinx.coroutines.launch
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.Gender
import com.judahben149.tala.domain.usecases.authentication.CreateDefaultUserDataUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateUserProfileUseCase
import com.judahben149.tala.util.AvatarUrlGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EmailVerificationViewModel(
    private val checkEmailVerificationUseCase: CheckEmailVerificationUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val firebaseService: FirebaseService,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createDefaultUserDataUseCase: CreateDefaultUserDataUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _verificationState = MutableStateFlow(EmailVerificationState())
    val verificationState: StateFlow<EmailVerificationState> = _verificationState.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()


    init {
        loadUserId()
        startVerificationPolling()
    }

    private fun loadUserId() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase()

            if (currentUser is Result.Success) {
                _userId.value = currentUser.data?.userId
            } else {
                _userId.value = null
            }
        }
    }

    private fun startVerificationPolling() {
        viewModelScope.launch {
            while (!_verificationState.value.isVerified) {
                delay(3000)
                checkEmailVerification()
            }
        }
    }

    private suspend fun checkEmailVerification() {
        when (val result = checkEmailVerificationUseCase()) {
            is Result.Success -> {
                if (result.data) {
                    // Email is verified! Refresh token first
                    val tokenRefreshed = firebaseService.refreshUserToken()

                    if (tokenRefreshed) {
                        logger.d { "Token refreshed successfully after verification" }
                        saveUserProfileData()

                        _verificationState.update {
                            it.copy(isVerified = true, canProceed = true)
                        }
                    } else {
                        _verificationState.update {
                            it.copy(error = "Failed to refresh authentication. Please try again.")
                        }
                    }
                }
            }
            is Result.Failure -> {
                _verificationState.update {
                    it.copy(error = "Failed to check verification status")
                }
            }
        }
    }

    private suspend fun saveUserProfileData() {
        try {
            val currentUser = firebaseService.getCurrentUser()

            if (currentUser != null) {
                val profileData = createDefaultUserDataUseCase(currentUser.copy(signInMethod = SignInMethod.EMAIL_PASSWORD))

                when (profileData) {
                    is Result.Success -> {
                        logger.d { "User profile saved successfully" }
                        updateUserProfileUseCase.saveUserProfile(currentUser.userId, profileData.data)
                    }
                    is Result.Failure -> {
                        logger.e(profileData.error) { "Failed to save user profile" }
                    }
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Failed to save user profile after verification" }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            sendEmailVerificationUseCase()
        }
    }
}

data class EmailVerificationState(
    val isVerified: Boolean = false,
    val canProceed: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)

