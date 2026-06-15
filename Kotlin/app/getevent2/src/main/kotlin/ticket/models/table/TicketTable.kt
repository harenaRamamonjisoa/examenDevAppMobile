package com.tco.ticket.models.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.date

object TicketTable : Table("ticket") {
    val idTicket = long("id").autoIncrement()
    val urlCode = varchar("url_code", 255)
    val dateGeneration = date("date_generation")
    val estUtilise = bool("est_utilise")
    val idReservation = long("id_reservation")
}
