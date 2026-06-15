package event.services

import event.models.CreateEventRequest
import event.models.Event
import event.models.UpdateEventRequest

interface EventService {

    suspend fun createEvent(request: CreateEventRequest): Event
    suspend fun getAllEvents(): List<Event>
    suspend fun getEventById(idEvent: Long): Result<Event>
    suspend fun deleteEvent(idEvent: Long): Result<Boolean>
    suspend fun updateEvent(idEvent: Long, request: UpdateEventRequest): Result<Event>


}