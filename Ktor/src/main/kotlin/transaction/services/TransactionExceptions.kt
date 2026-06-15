package transaction.services

class TransactionNotFoundException(message: String) : Exception(message)
class TransactionUpdateException(message: String) : Exception(message)
class TransactionValidationException(val errors: List<String>) : Exception(errors.joinToString(", "))
class TransactionAlreadyExistsException(message: String) : Exception(message)
class ReservationNotLinkedException(message: String) : Exception(message)
