package com.tco.mapper

import com.tco.ticket.models.Ticket
import com.tco.ticket.models.table.TicketTable
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toTicket(): Ticket {
    return Ticket(
        idTicket = this[TicketTable.idTicket],
        urlCode = this[TicketTable.urlCode],
        dateGeneration = this[TicketTable.dateGeneration],
        estUtilise = this[TicketTable.estUtilise],
        idReservation = this[TicketTable.idReservation]
    )
}
