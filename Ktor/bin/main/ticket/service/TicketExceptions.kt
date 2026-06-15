package com.tco.ticket.service

class TicketNotFoundException(message: String) : Exception(message)
class TicketUpdateException(message: String) : Exception(message)
class TicketValidationException(val errors: List<String>) : Exception(errors.joinToString(", "))
class TicketAlreadyExistsException(message: String) : Exception(message)
class TicketAlreadyUsedException(message: String) : Exception(message)
class PaymentNotConfirmedException(message: String) : Exception(message)
