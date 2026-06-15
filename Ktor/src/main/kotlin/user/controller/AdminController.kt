package com.tco.user.controller

import com.tco.dto.request.UpdateStatusRequest
import com.tco.security.isAdmin
import event.models.ApiResponse
import event.models.ErrorResponse
import event.services.EventService
import com.tco.dto.response.StatisticsResponse
import com.tco.user.service.UserService
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.get
import io.ktor.server.response.respond
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.patch
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import reservation.models.FiltrePaiement
import reservation.services.EventNotAvailableException
import reservation.services.ReservationService

fun Route.adminController(
    eventService: EventService,
    reservationService: ReservationService,
    userService: UserService,
) {
    authenticate("auth-jwt") {
        route("/admin") {
            get("/statistics") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Non authentifié"))

                if (!principal.isAdmin()) {
                    return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse(message = "Accès réservé aux administrateurs"))
                }

                val totalEvents = eventService.getAllEvents().size
                val totalReservations = reservationService.getAllReservations().size
                val totalUsers = userService.findAllUsers()?.size

                val stats = StatisticsResponse(totalEvents, totalReservations, totalUsers)
                call.respond(HttpStatusCode.OK, stats)
            }

            patch("/user/{id}/statut") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@patch call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Non authentifié"))

                if (!principal.isAdmin()) {
                    return@patch call.respond(HttpStatusCode.Forbidden, ErrorResponse(message = "Accès réservé aux administrateurs"))
                }

                val userId = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = "ID invalide"))

                val request = call.receive<UpdateStatusRequest>()

                try {
                    userService.changeStatusUserForAdmin(userId, request.statut.toString())
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Le statut a bien été mis à jour"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.localizedMessage))
                }
            }

            route("/events/{idEvent}/reservations") {
                get {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Non authentifié"))

                    if (!principal.isAdmin()) {
                        return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse(message = "Accès réservé aux administrateurs"))
                    }

                    val idEvent = call.parameters["idEvent"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = "ID événement invalide"))

                    val filtreParam = call.request.queryParameters["filtre"]
                    val filtre = FiltrePaiement.fromQuery(filtreParam)
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = "Filtre invalide. Valeurs acceptées : tous, non_paye, en_attente, paye")
                        )

                    try {
                        val reservations = reservationService.getEventReservationsForAdmin(idEvent, filtre)
                        call.respond(
                            ApiResponse(
                                success = true,
                                message = "${reservations.size} réservation(s) trouvée(s)",
                                data = reservations
                            )
                        )
                    } catch (e: EventNotAvailableException) {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(message = e.message ?: "Événement non trouvé"))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = e.message ?: "Requête invalide"))
                    }
                }

                get("/non-payees") {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Non authentifié"))

                    if (!principal.isAdmin()) {
                        return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse(message = "Accès réservé aux administrateurs"))
                    }

                    val idEvent = call.parameters["idEvent"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = "ID événement invalide"))

                    try {
                        val nonPayees = reservationService.getEventReservationsForAdmin(idEvent, FiltrePaiement.NON_PAYE)
                        val enAttente = reservationService.getEventReservationsForAdmin(idEvent, FiltrePaiement.EN_ATTENTE)
                        val reservations = nonPayees + enAttente

                        call.respond(
                            ApiResponse(
                                success = true,
                                message = "${reservations.size} étudiant(s) ont réservé sans avoir payé",
                                data = reservations
                            )
                        )
                    } catch (e: EventNotAvailableException) {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(message = e.message ?: "Événement non trouvé"))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = e.message ?: "Requête invalide"))
                    }
                }

                get("/payees") {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Non authentifié"))

                    if (!principal.isAdmin()) {
                        return@get call.respond(HttpStatusCode.Forbidden, ErrorResponse(message = "Accès réservé aux administrateurs"))
                    }

                    val idEvent = call.parameters["idEvent"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = "ID événement invalide"))

                    try {
                        val reservations = reservationService.getEventReservationsForAdmin(idEvent, FiltrePaiement.PAYE)
                        call.respond(
                            ApiResponse(
                                success = true,
                                message = "${reservations.size} étudiant(s) ont payé ou ont une réservation gratuite",
                                data = reservations
                            )
                        )
                    } catch (e: EventNotAvailableException) {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse(message = e.message ?: "Événement non trouvé"))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = e.message ?: "Requête invalide"))
                    }
                }
            }
        }
    }
}
