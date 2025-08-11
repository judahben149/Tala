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
    
    fun updatePassword(password: String) {
        _formState.value = _formState.value.copy(password = password)
    }
    
    fun updateDisplayName(displayName: String) {
        _formState.value = _formState.value.copy(displayName = displayName)
    }
    
    fun signUp() {
        val currentState = _formState.value
        
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = createUserUseCase(
                email = currentState.email,
                password = currentState.password,
                displayName = currentState.displayName
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
    val password: String = "",
    val displayName: String = ""
)
