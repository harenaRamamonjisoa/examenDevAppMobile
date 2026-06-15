package com.tco

import com.tco.ticket.routes.ticketRoutes
import com.tco.ticket.service.TicketService
import com.tco.user.controller.adminController
import com.tco.user.controller.authController
import com.tco.user.controller.userController
import com.tco.user.service.AuthService
import com.tco.user.service.UserService
import event.routes.eventRoutes
import event.services.EventService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.*
import reservation.routes.reservationRoutes
import reservation.services.ReservationService
import transaction.routes.transactionRoutes
import transaction.services.TransactionService

fun Application.configureRouting(
    eventService: EventService,
    reservationService: ReservationService,
    userService: UserService,
    authService: AuthService,
    ticketService: TicketService,
    transactionService: TransactionService
) {
    routing {

        eventRoutes(eventService)
        reservationRoutes(reservationService)
        ticketRoutes(ticketService)
        transactionRoutes(transactionService)

        userController(userService, reservationService)
        authController(authService)
        adminController(eventService, reservationService, userService)

        get("/") {
            call.respondText("Hello, World!")
        }
        get<Articles> { article ->
            call.respond("List of articles sorted starting from ${article.sort}")
        }
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
