package com.judahben149.tala.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.authentication.IsUserSignedInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.judahben149.tala.domain.models.common.Result

class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isUserSignedInUseCase: IsUserSignedInUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState<AppUser?, FirebaseError>>(UiState.Loading)
    val uiState: StateFlow<UiState<AppUser?, FirebaseError>> = _uiState.asStateFlow()
    
    private val _signInState = MutableStateFlow<UiState<AppUser, FirebaseError>?>(null)
    val signInState: StateFlow<UiState<AppUser, FirebaseError>?> = _signInState.asStateFlow()
    
    init {
        checkCurrentUser()
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _signInState.value = UiState.Loading
            
            when (val result = signInUseCase(email, password)) {
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
    
    fun isUserSignedIn(): Boolean = isUserSignedInUseCase()
}
