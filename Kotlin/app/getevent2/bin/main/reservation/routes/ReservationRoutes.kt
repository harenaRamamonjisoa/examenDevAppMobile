package reservation.routes

import event.models.ApiResponse
import event.models.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import reservation.models.CreateReservationRequest
import reservation.models.UpdateReservationRequest
import reservation.services.EventNotAvailableException
import reservation.services.ReservationNotFoundException
import reservation.services.ReservationService
import reservation.services.ReservationValidationException

fun Route.reservationRoutes(reservationService: ReservationService) {

    route("/api/reservations") {

        // LISTE TOUTES LES RÉSERVATIONS
        get {
            val reservations = reservationService.getAllReservations()
            call.respond(
                ApiResponse(
                    success = true,
                    message = "${reservations.size} réservation(s) trouvée(s)",
                    data = reservations
                )
            )
        }

        // DÉTAIL D'UNE RÉSERVATION
        get("/{idReservation}") {
            val idReservation = call.parameters["idReservation"]?.toLongOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            reservationService.getReservationById(idReservation)
                .onSuccess { reservation ->
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "Réservation trouvée",
                            data = reservation
                        )
                    )
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(message = error.message ?: "Erreur")
                    )
                }
        }

        // RÉSERVATIONS PAR ÉVÉNEMENT
        get("/event/{idEvent}") {
            val idEvent = call.parameters["idEvent"]?.toLongOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID événement invalide")
                )

            val reservations = reservationService.getReservationsByEvent(idEvent)
            call.respond(
                ApiResponse(
                    success = true,
                    message = "${reservations.size} réservation(s) pour cet événement",
                    data = reservations
                )
            )
        }

        // CRÉER UNE RÉSERVATION
        post {
            try {
                val request = call.receive<CreateReservationRequest>()
                reservationService.createReservation(request)
                    .onSuccess { created ->
                        call.respond(
                            HttpStatusCode.Created,
                            ApiResponse(
                                success = true,
                                message = "Réservation créée avec succès",
                                data = created
                            )
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = error.message ?: "Erreur")
                        )
                    }
            } catch (e: ReservationValidationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "Validation échouée", errors = e.errors)
                )
            }
        }

        // MODIFIER UNE RÉSERVATION
        put("/{idReservation}") {
            val idReservation = call.parameters["idReservation"]?.toLongOrNull()
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            val request = call.receive<UpdateReservationRequest>()

            reservationService.updateReservation(idReservation, request)
                .onSuccess { updated ->
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "Réservation mise à jour",
                            data = updated
                        )
                    )
                }
                .onFailure { error ->
                    when (error) {
                        is ReservationNotFoundException -> call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(message = error.message ?: "Non trouvée")
                        )
                        is EventNotAvailableException -> call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = error.message ?: "Erreur")
                        )
                        else -> call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse(message = error.message ?: "Erreur")
                        )
                    }
                }
        }

        // SUPPRIMER UNE RÉSERVATION
        delete("/{idReservation}") {
            val idReservation = call.parameters["idReservation"]?.toLongOrNull()
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            reservationService.deleteReservation(idReservation)
                .onSuccess {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse<Unit>(
                            success = true,
                            message = "Réservation supprimée avec succès",
                            data = null
                        )
                    )
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(message = error.message ?: "Erreur")
                    )
                }
        }
    }
}
