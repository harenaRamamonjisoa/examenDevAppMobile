package com.example.getevent.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getevent.data.local.SessionManager
import com.example.getevent.data.remote.dto.LoginRequest
import com.example.getevent.data.remote.dto.Role
import com.example.getevent.data.remote.dto.UserResponse
import com.example.getevent.data.repository.AuthRepository
import com.example.getevent.util.JwtUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    private val _loginSuccess = MutableSharedFlow<UserResponse>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun onLoginClick() {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                error = "Please fill all fields"
                return@launch
            }
            
            isLoading = true
            error = null
            val result = repository.login(LoginRequest(email, password))
            isLoading = false
            
            result.onSuccess { response ->
                val roleStr = JwtUtils.getRoleFromToken(response.token) ?: "STUDENT"
                val role = try { Role.valueOf(roleStr) } catch (e: Exception) { Role.STUDENT }
                
                sessionManager.saveAuthToken(response.token)
                sessionManager.saveUserRole(role.name)
                
                _loginSuccess.emit(response.user.copy(role = role))
            }.onFailure { e ->
                error = e.message ?: "Authentication failed"
            }
        }
    }
}
