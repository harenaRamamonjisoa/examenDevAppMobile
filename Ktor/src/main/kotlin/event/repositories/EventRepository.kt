package event.repositories

import event.models.Event

interface EventRepository {
    suspend fun findAll(): List<Event>
    suspend fun findById(idEvent: Long): Event?
    suspend fun save(event: Event): Event
    suspend fun update(idEvent: Long, event: Event): Event?
    suspend fun delete(idEvent: Long): Boolean
}