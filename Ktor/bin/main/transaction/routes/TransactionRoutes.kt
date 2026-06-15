package transaction.routes

import event.models.ApiResponse
import event.models.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import transaction.models.CreateTransactionRequest
import transaction.models.UpdateTransactionRequest
import transaction.services.ReservationNotLinkedException
import transaction.services.TransactionAlreadyExistsException
import transaction.services.TransactionNotFoundException
import transaction.services.TransactionService
import transaction.services.TransactionValidationException

fun Route.transactionRoutes(transactionService: TransactionService) {

    route("/api/transactions") {

        get {
            val transactions = transactionService.getAllTransactions()
            call.respond(
                ApiResponse(
                    success = true,
                    message = "${transactions.size} transaction(s) trouvée(s)",
                    data = transactions
                )
            )
        }

        get("/{idTransaction}") {
            val idTransaction = call.parameters["idTransaction"]?.toLongOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            transactionService.getTransactionById(idTransaction)
                .onSuccess { transaction ->
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "Transaction trouvée",
                            data = transaction
                        )
                    )
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(message = error.message ?: "Erreur")
                    )
                }
        }

        get("/reservation/{idReservation}") {
            val idReservation = call.parameters["idReservation"]?.toLongOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID réservation invalide")
                )

            transactionService.getTransactionByReservation(idReservation)
                .onSuccess { transaction ->
                    call.respond(
                        ApiResponse(
                            success = true,
                            message = "Transaction trouvée",
                            data = transaction
                        )
                    )
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(message = error.message ?: "Erreur")
                    )
                }
        }

        post {
            try {
                val request = call.receive<CreateTransactionRequest>()
                transactionService.createTransaction(request)
                    .onSuccess { created ->
                        call.respond(
                            HttpStatusCode.Created,
                            ApiResponse(
                                success = true,
                                message = "Transaction créée avec succès",
                                data = created
                            )
                        )
                    }
                    .onFailure { error ->
                        val status = when (error) {
                            is TransactionAlreadyExistsException,
                            is ReservationNotLinkedException -> HttpStatusCode.BadRequest
                            else -> HttpStatusCode.BadRequest
                        }
                        call.respond(status, ErrorResponse(message = error.message ?: "Erreur"))
                    }
            } catch (e: TransactionValidationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "Validation échouée", errors = e.errors)
                )
            }
        }

        put("/{idTransaction}") {
            val idTransaction = call.parameters["idTransaction"]?.toLongOrNull()
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            val request = call.receive<UpdateTransactionRequest>()

            transactionService.updateTransaction(idTransaction, request)
                .onSuccess { updated ->
                    call.respond(
                        ApiResponse(
                            success = true,
                            message = "Transaction mise à jour",
                            data = updated
                        )
                    )
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(message = error.message ?: "Erreur")
                    )
                }
        }

        patch("/{idTransaction}/confirm") {
            val idTransaction = call.parameters["idTransaction"]?.toLongOrNull()
                ?: return@patch call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            transactionService.confirmTransaction(idTransaction)
                .onSuccess { confirmed ->
                    call.respond(
                        ApiResponse(
                            success = true,
                            message = "Paiement confirmé, ticket généré",
                            data = confirmed
                        )
                    )
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(message = error.message ?: "Erreur")
                    )
                }
        }

        delete("/{idTransaction}") {
            val idTransaction = call.parameters["idTransaction"]?.toLongOrNull()
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = "ID invalide")
                )

            transactionService.deleteTransaction(idTransaction)
                .onSuccess {
                    call.respond(
                        ApiResponse<Unit>(
                            success = true,
                            message = "Transaction supprimée avec succès",
                            data = null
                        )
                    )
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(message = error.message ?: "Erreur")
                    )
                }
        }
    }
}
