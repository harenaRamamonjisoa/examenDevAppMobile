package com.tco.user.controller

import com.tco.dto.request.UpdateProfilRequest
import com.tco.user.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route

fun Route.userController(userService: UserService) {
authenticate ("auth-jwt"){
    route("/me") {

        /**
         * 1. GET /me/status
         * Rôle : Permet à l'application mobile de faire du Polling en boucle
         * pour vérifier l'état de l'inscription (PENDING, AUTHORIZED, REFUSED).
         */
        get("/status") {

            val principal = call.principal<JWTPrincipal>()

            val userId = principal?.getClaim("userId", String::class)?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Jeton invalide ou ID manquant")


            try {
                val statutActuel = userService.verifyStatusForMobile(userId)
                call.respond(HttpStatusCode.OK, mapOf("statut" to statutActuel.name))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.localizedMessage))
            }
        }

        /**
         * 2. PATCH /me/profile
         * Rôle : Permet à l'utilisateur de modifier partiellement ses données de profil.
         */
        patch("/profile") {

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asString()?.toLongOrNull()
                ?: return@patch call.respond(HttpStatusCode.Unauthorized, "Jeton invalide ou ID manquant")

            try {
                val request = call.receive<UpdateProfilRequest>()

                // Appel au service pour appliquer la modification partielle
                val userMisAJour = userService.setProfile(userId, request)

                call.respond(HttpStatusCode.OK, userMisAJour)
            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.localizedMessage))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.localizedMessage))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.localizedMessage))
            }
        }
    }
}

}