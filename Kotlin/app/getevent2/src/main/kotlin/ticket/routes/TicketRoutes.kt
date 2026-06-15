package com.tco.ticket.routes

import com.tco.ticket.models.CreateTicketRequest
import com.tco.ticket.service.PaymentNotConfirmedException
import com.tco.ticket.service.TicketAlreadyExistsException
import com.tco.ticket.service.TicketAlreadyUsedException
import com.tco.ticket.service.TicketNotFoundException
import com.tco.ticket.service.TicketService
import com.tco.ticket.service.TicketValidationException
import event.models.ApiResponse
import event.models.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.ticketRoutes(ticketService: TicketService) {

    route("/api/tickets") {

        get {
            val tickets = ticketService.getAllTickets()
            call.respond(
                ApiResponse(
                    success = true,
                    message = "${tickets.size} ticket(s) trouvé(s)",
                    data = tickets
                )
            )
        }

        get("/{idTicket}") {
            val idTicket = call.parameters["idTicket"]?.toLongOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            ticketService.getTicketById(idTicket)
                .onSuccess { ticket ->
                    call.respond(
                        ApiResponse(
                            success = true,
                            message = "Ticket trouvé",
                            data = ticket
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

        get("/reservation/{idReservation}") {
            val idReservation = call.parameters["idReservation"]?.toLongOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID réservation invalide")
                )

            ticketService.getTicketByReservation(idReservation)
                .onSuccess { ticket ->
                    call.respond(
                        ApiResponse(
                            success = true,
                            message = "Ticket trouvé",
                            data = ticket
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

        get("/qrcode/{urlCode}") {
            val urlCode = call.parameters["urlCode"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "QR code invalide")
                )

            ticketService.getTicketByUrlCode(urlCode)
                .onSuccess { ticket ->
                    call.respond(
                        ApiResponse(
                            success = true,
                            message = "Ticket trouvé",
                            data = ticket
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

        post {
            try {
                val request = call.receive<CreateTicketRequest>()
                ticketService.generateTicket(request)
                    .onSuccess { created ->
                        call.respond(
                            HttpStatusCode.Created,
                            ApiResponse(
                                success = true,
                                message = "Ticket généré avec succès",
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
            } catch (e: TicketValidationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "Validation échouée", errors = e.errors)
                )
            }
        }

        patch("/use/{urlCode}") {
            val urlCode = call.parameters["urlCode"]
                ?: return@patch call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "QR code invalide")
                )

            ticketService.useTicket(urlCode)
                .onSuccess { ticket ->
                    call.respond(
                        ApiResponse(
                            success = true,
                            message = "Ticket validé avec succès",
                            data = ticket
                        )
                    )
                }
                .onFailure { error ->
                    val status = when (error) {
                        is TicketAlreadyUsedException -> HttpStatusCode.Conflict
                        is TicketNotFoundException -> HttpStatusCode.NotFound
                        else -> HttpStatusCode.BadRequest
                    }
                    call.respond(status, ErrorResponse(message = error.message ?: "Erreur"))
                }
        }

        delete("/{idTicket}") {
            val idTicket = call.parameters["idTicket"]?.toLongOrNull()
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            ticketService.deleteTicket(idTicket)
                .onSuccess {
                    call.respond(
                        ApiResponse<Unit>(
                            success = true,
                            message = "Ticket supprimé avec succès",
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
