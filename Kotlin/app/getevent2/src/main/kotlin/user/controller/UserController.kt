package com.tco.user.controller

import com.tco.dto.request.UpdateProfilRequest
import com.tco.security.userId
import com.tco.user.service.UserService
import event.models.ApiResponse
import event.models.ErrorResponse
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
import reservation.services.EventNotAvailableException
import reservation.services.ReservationService

fun Route.userController(userService: UserService, reservationService: ReservationService) {
    authenticate("auth-jwt") {
        route("/me") {

            get("/status") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Jeton invalide ou ID manquant")

                try {
                    val statutActuel = userService.verifyStatusForMobile(userId)
                    call.respond(HttpStatusCode.OK, mapOf("statut" to statutActuel.name))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.localizedMessage))
                }
            }

            get("/reservations") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Jeton invalide ou ID manquant"))

                try {
                    val reservations = reservationService.getReservationsByUser(userId)
                    call.respond(
                        ApiResponse(
                            success = true,
                            message = "${reservations.size} réservation(s) trouvée(s)",
                            data = reservations
                        )
                    )
                } catch (e: EventNotAvailableException) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(message = e.message ?: "Événement non trouvé"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(message = e.localizedMessage))
                }
            }

            patch("/profile") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.userId()
                    ?: return@patch call.respond(HttpStatusCode.Unauthorized, "Jeton invalide ou ID manquant")

                try {
                    val request = call.receive<UpdateProfilRequest>()
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
