package com.tco.profile.routes

import com.tco.user.repository.UserRepository
import com.tco.user.model.toResponse
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.get
import io.ktor.server.response.respond
import io.ktor.http.HttpStatusCode

fun Route.registerProfileRoutes(userRepository: com.tco.user.repository.UserRepository) {
    route("/users") {
        get {
            val users = userRepository.findAll().map { it.toResponse() }
            call.respond(HttpStatusCode.OK, users)
        }
    }
}
