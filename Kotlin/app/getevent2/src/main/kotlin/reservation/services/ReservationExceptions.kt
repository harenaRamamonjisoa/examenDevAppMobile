package reservation.services

class ReservationNotFoundException(message: String) : Exception(message)
class ReservationUpdateException(message: String) : Exception(message)
class ReservationValidationException(val errors: List<String>) : Exception(errors.joinToString(", "))
class EventNotAvailableException(message: String) : Exception(message)