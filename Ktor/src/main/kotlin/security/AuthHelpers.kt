package com.tco.security

import com.tco.user.model.Role
import io.ktor.server.auth.jwt.JWTPrincipal

fun JWTPrincipal.userId(): Long? = getClaim("userId", String::class)?.toLongOrNull()

fun JWTPrincipal.role(): Role? = getClaim("role", String::class)?.let { runCatching { Role.valueOf(it) }.getOrNull() }

fun JWTPrincipal.isAdmin(): Boolean = role() == Role.ADMIN
