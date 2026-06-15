package com.tco.mapper

import com.tco.reservation.models.table.ReservationTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.javatime.time
import reservation.models.Reservation
import reservation.models.StatutReservation

fun ResultRow.toReservation(): Reservation {
    return Reservation(
        this[ReservationTable.idReservation],
        this[ReservationTable.dateReservation],
        this[ReservationTable.idEvent],
        StatutReservation.valueOf(this[ReservationTable.statut])
    )
}