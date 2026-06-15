package com.example.getevent.data.repository

import com.example.getevent.data.remote.ApiService
import com.example.getevent.data.remote.dto.LoginRequest
import com.example.getevent.data.remote.dto.LoginResponse
import com.example.getevent.data.remote.dto.RegisterRequest
import com.example.getevent.data.remote.dto.UserResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerStudent(request: RegisterRequest): Result<UserResponse> {
        return try {
            val response = apiService.registerStudent(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
