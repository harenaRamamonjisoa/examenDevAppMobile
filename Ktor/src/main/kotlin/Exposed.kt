package com.tco

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

suspend fun Application.configureExposed() {
    fun init() {

    }

    R2dbcDatabase.connect(
        url = environment.config.property("storage.url").getString(),

        user = environment.config.property("storage.user").getString(),

        password = environment.config.property("storage.password").getString()
    )




}



