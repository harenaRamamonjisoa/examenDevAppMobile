package com.tco.user.controller

import com.tco.dto.request.RegisterStudentRequest
import com.tco.user.model.Role

import com.tco.user.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route


import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.authController(authService: AuthService) {
    route("/user"){
        post("/register-student") {
            val request = call.receive<RegisterStudentRequest>()

            val response = authService.registerUser(request, Role.STUDENT)

            call.respond(HttpStatusCode.Created,response )
        }

        post("/login") {
            val request = call.receive<com.tco.dto.request.LoginRequest>()
            try {
                val response = authService.login(request.email, request.password)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to (e.message ?: "Invalid credentials")))
            }
        }

        post("/register-staff"){
            val request = call.receive<RegisterStudentRequest>()

            val response = authService.registerUser(request, Role.BUREAU_MEMBRER)

            call.respond(HttpStatusCode.Created,response )
        }


    }

}

