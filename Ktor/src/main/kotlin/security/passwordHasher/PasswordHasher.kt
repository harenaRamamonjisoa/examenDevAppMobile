package com.tco.security.passwordHasher

interface PasswordHasher {
    fun hash(password: String): String
    fun verify(password: String,
               hashedPassword: String): Boolean
}