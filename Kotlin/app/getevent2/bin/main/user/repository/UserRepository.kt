package com.tco.user.repository


import com.tco.user.model.User

interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: Long): User?
    suspend fun createUser(user: User) : User?
    suspend fun update(user: User)
    suspend fun deleteById(id: Long) : Boolean
    suspend fun findAll(): List<User>
}