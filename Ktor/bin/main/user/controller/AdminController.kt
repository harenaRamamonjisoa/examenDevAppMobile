package com.tco.user.controller

import com.tco.dto.request.UpdateStatusRequest
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
import reservation.services.ReservationService

fun Route.adminController(
    eventService: EventService,
    reservationService: ReservationService,
    userService: UserService,

    ) {
    route("/admin") {
        get("/statistics") {
            val totalEvents = eventService.getAllEvents().size
            val totalReservations = reservationService.getAllReservations().size
            val totalUsers = userService.findAllUsers()?.size

            val stats = StatisticsResponse(totalEvents, totalReservations, totalUsers)
            call.respond(HttpStatusCode.OK, stats)
        }
        patch("user/{id}/statut") {
            val userId = call.parameters["id"] ?.toLongOrNull()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "ID invalide")

            val request = call.receive<UpdateStatusRequest>() // Récupère {"status": "REFUSED"}

            try {
                userService.changeStatusUserForAdmin(userId, request.statut.toString())
                call.respond(HttpStatusCode.OK, mapOf("message" to "Le statut a bien été mis à jour"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.localizedMessage))
            }


        }

    }
}