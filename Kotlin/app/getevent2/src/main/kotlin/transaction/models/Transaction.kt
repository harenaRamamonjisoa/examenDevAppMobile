package transaction.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Transaction(
    val idTransaction: Long,
    val montant: Double,
    @Contextual
    val dateTransaction: Instant,
    val modePaiement: String,
    val statutPaiement: StatutPaiement,
    val referencePaiement: Long,
    val idReservation: Long
)
