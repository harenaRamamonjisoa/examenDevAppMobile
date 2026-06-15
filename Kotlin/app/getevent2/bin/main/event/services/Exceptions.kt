package event.services

class EventNotFoundException(message: String) : Exception(message)
class EventUpdateException(message: String) : Exception(message)
class ValidationException(val errors: List<String>) : Exception(errors.joinToString(", "))