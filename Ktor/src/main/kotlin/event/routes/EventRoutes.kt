package event.routes

import event.models.CreateEventRequest
import event.models.UpdateEventRequest
import event.services.EventNotFoundException
import event.services.EventService
import event.services.ValidationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import event.models.ApiResponse
import event.models.ErrorResponse

fun Route.eventRoutes(eventService: EventService) {

    route("/api/events") {

        // LISTE TOUS LES ÉVÉNEMENTS
        get {
            val events = eventService.getAllEvents()
            call.respond(
                ApiResponse(
                    success = true,
                    message = "${events.size} événement(s) trouvé(s)",
                    data = events
                )
            )
        }

        // DÉTAIL D'UN ÉVÉNEMENT
        get("/{idEvent}") {
            val idEvent = call.parameters["idEvent"]?.toLongOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            eventService.getEventById(idEvent)
                .onSuccess { event ->
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "Événement trouvé",
                            data = event
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

        // CRÉER UN ÉVÉNEMENT
        post {
            try {
                val request = call.receive<CreateEventRequest>()
                val created = eventService.createEvent(request)

                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        success = true,
                        message = "Événement créé avec succès",
                        data = created
                    )
                )
            } catch (e: ValidationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        message = "Validation échouée",
                        errors = e.errors
                    )
                )
            }
        }

        // MODIFIER UN ÉVÉNEMENT
        put("/{idEvent}") {
            val idEvent = call.parameters["idEvent"]?.toLongOrNull()
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            val request = call.receive<UpdateEventRequest>()

            eventService.updateEvent(idEvent, request)
                .onSuccess { updated ->
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "Événement mis à jour",
                            data = updated
                        )
                    )
                }
                .onFailure { error ->
                    when (error) {
                        is EventNotFoundException -> call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(message = error.message ?: "Non trouvé")
                        )
                        else -> call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse(message = error.message ?: "Erreur")
                        )
                    }
                }
        }

        // SUPPRIMER UN ÉVÉNEMENT
        delete("/{idEvent}") {
            val idEvent = call.parameters["idEvent"]?.toLongOrNull()
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            eventService.deleteEvent(idEvent)
                .onSuccess {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse<Unit>(
                            success = true,
                            message = "Événement supprimé avec succès",
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