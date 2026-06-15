package transaction.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTransactionRequest(
    val montant: Double? = null,
    val modePaiement: String? = null,
    val statutPaiement: StatutPaiement? = null,
    val referencePaiement: Long? = null
)
