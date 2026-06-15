package com.tco.user.service.impl

import com.tco.dto.request.RegisterStudentRequest
import com.tco.dto.response.UserResponse
import com.tco.user.model.Role
import com.tco.user.model.Statut
import com.tco.user.model.User
import com.tco.user.model.toResponse
import com.tco.user.service.AuthService
import com.tco.user.repository.UserRepository
import com.tco.security.passwordHasher.PasswordHasher
import com.tco.dto.response.LoginResponse
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tco.user.model.toResponse
import java.util.Date

class AuthServiceImpl(private val userRepository: UserRepository, private val passwordHasher : PasswordHasher) :
    com.tco.user.service.AuthService {

    override suspend fun registerUser(registerStudentRequest: RegisterStudentRequest, role : com.tco.user.model.Role): UserResponse {

        if (userRepository.findByEmail(registerStudentRequest.email) != null) {
            throw IllegalArgumentException("Utilisateur existe déjà !")
        }
        if (!registerStudentRequest.email.contains('@')) throw IllegalArgumentException("Email invalide!")

        if (!registerStudentRequest.phoneNumber.contains("+261")) throw IllegalArgumentException("Numero de telephone invalide!")

        if (!registerStudentRequest.studentId.contains('/')) throw IllegalArgumentException("Identifiant etudiant invalide!")

        var statut = Statut.APPROVED

        if (role == Role.BUREAU_MEMBRER) {
            statut = Statut.PENDING
        }


        val newUser = User(
            id = 0,
            email = registerStudentRequest.email,
            firstName = registerStudentRequest.firstName,
            lastName = registerStudentRequest.lastName,
            phoneNumber = registerStudentRequest.phoneNumber,
            studentId = registerStudentRequest.studentId,
            hashedPassword = passwordHasher.hash(registerStudentRequest.password),
            role = role,
            statut = statut
        )
        val newUserCreated = userRepository.createUser(newUser)

        return newUserCreated?.toResponse() ?: throw IllegalStateException("Erreur lors de la création de l'utilisateur")
    }

    override suspend fun login(username: String, password: String): LoginResponse {
        val user = userRepository.findByEmail(username) ?: throw IllegalArgumentException("Identifiants incorrects")
        
        if (!passwordHasher.verify(password, user.hashedPassword)) {
            throw IllegalArgumentException("Identifiants incorrects")
        }

        val jwtSecret = "secret"
        val token = JWT.create()
            .withAudience("jwt-audience")
            .withIssuer("https://jwt-provider-domain/")
            .withClaim("userId", user.id.toString())
            .withClaim("email", user.email)
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000000))
            .sign(Algorithm.HMAC256(jwtSecret))

        return LoginResponse(token, user.toResponse())
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }


}