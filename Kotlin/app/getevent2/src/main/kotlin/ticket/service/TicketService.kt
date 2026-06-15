package com.tco.ticket.service

import com.tco.ticket.models.CreateTicketRequest
import com.tco.ticket.models.Ticket

interface TicketService {
    suspend fun getAllTickets(): List<Ticket>
    suspend fun getTicketById(idTicket: Long): Result<Ticket>
    suspend fun getTicketByReservation(idReservation: Long): Result<Ticket>
    suspend fun getTicketByUrlCode(urlCode: String): Result<Ticket>
    suspend fun generateTicket(request: CreateTicketRequest): Result<Ticket>
    suspend fun useTicket(urlCode: String): Result<Ticket>
    suspend fun deleteTicket(idTicket: Long): Result<Boolean>
}
