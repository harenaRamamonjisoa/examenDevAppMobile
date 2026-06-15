package transaction.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateTransactionRequest(
    val montant: Double,
    val modePaiement: String,
    val referencePaiement: Long,
    val idReservation: Long
)
