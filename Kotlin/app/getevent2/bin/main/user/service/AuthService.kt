package com.tco.user.service

import com.tco.dto.request.RegisterStudentRequest
import com.tco.dto.response.UserResponse
import com.tco.user.model.Role

import com.tco.dto.response.LoginResponse

interface AuthService {

    suspend fun login(email: String, password: String): LoginResponse

    suspend fun registerUser(registerStudentRequest: RegisterStudentRequest, role : com.tco.user.model.Role): UserResponse

    suspend fun logout()

}