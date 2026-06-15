package com.tco.reservation.models.table


import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.date

object ReservationTable : Table() {
    val idReservation = long("id").autoIncrement()
    val dateReservation = date("date")
    val idEvent = long("idEvent")
    val idUser = long("id_user")
    val statut = varchar("statut", 25)

}