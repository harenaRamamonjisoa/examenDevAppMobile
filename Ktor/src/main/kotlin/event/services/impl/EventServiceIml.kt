package event.services.impl


import event.models.Event
import event.models.UpdateEventRequest
import event.repositories.EventRepository
import event.models.CreateEventRequest
import event.services.EventNotFoundException
import event.services.EventService
import event.services.EventUpdateException
import event.services.ValidationException
import java.time.LocalDate
class EventServiceImpl(
    private val repository: EventRepository
): EventService {

    // READ - Tous les événements
    override suspend fun getAllEvents(): List<Event> {
        return repository.findAll()
    }

    // READ - Un événement par ID
    override suspend fun getEventById(idEvent: Long): Result<Event> {
        val event = repository.findById(idEvent)
        return if (event != null) {
            Result.success(event)
        } else {
            Result.failure(EventNotFoundException("Événement avec l'ID $idEvent non trouvé"))
        }
    }

    // CREATE - Nouvel événement
    override suspend fun createEvent(request: CreateEventRequest): Event {
        validateCreateRequest(request)

        val event = Event(
            idEvent = 0,
            nomEvent = request.nomEvent,
            dateEvent = request.dateEvent,
            lieu = request.lieu,
            description = request.description,
            nombreParticipants = request.nombreParticipants,
            estPrive = request.estPrive
        )

        return repository.save(event)
    }

    // UPDATE - Modifier un événement
    override suspend fun updateEvent(idEvent: Long, request: UpdateEventRequest): Result<Event> {
        val existingEvent = repository.findById(idEvent)
            ?: return Result.failure(EventNotFoundException("Événement avec l'ID $idEvent non trouvé"))

        val updatedEvent = existingEvent.copy(
            nomEvent = request.nomEvent ?: existingEvent.nomEvent,
            dateEvent = (request.dateEvent ?: existingEvent.dateEvent) as LocalDate,
            lieu = request.lieu ?: existingEvent.lieu,
            description = request.description ?: existingEvent.description,
            nombreParticipants = request.nombreParticipants ?: existingEvent.nombreParticipants,
            estPrive = request.estPrive ?: existingEvent.estPrive
        )

        val result = repository.update(idEvent, updatedEvent)
        return if (result != null) {
            Result.success(result)
        } else {
            Result.failure(EventUpdateException("Erreur lors de la mise à jour de l'événement"))
        }
    }

    // DELETE - Supprimer un événement
    override suspend fun deleteEvent(idEvent: Long): Result<Boolean> {
        return if (repository.delete(idEvent)) {
            Result.success(true)
        } else {
            Result.failure(EventNotFoundException("Événement avec l'ID $idEvent non trouvé"))
        }
    }

    // VALIDATION
    private fun validateCreateRequest(request: CreateEventRequest) {
        val errors = mutableListOf<String>()

        if (request.nomEvent.isBlank()) {
            errors.add("Le nom de l'événement ne peut pas être vide")
        }
        if (request.description.isBlank()) {
            errors.add("La description ne peut pas être vide")
        }
        if (request.lieu.nom.isBlank()) {
            errors.add("Le nom du lieu est obligatoire")
        }
        if (request.lieu.capacite < 1) {
            errors.add("La capacité du lieu doit être positive")
        }

        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }
}

