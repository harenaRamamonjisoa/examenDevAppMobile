package transaction.models

import kotlinx.serialization.Serializable

@Serializable
enum class StatutPaiement {
    EN_ATTENTE,
    CONFIRME,
    ECHOUE,
    ANNULE
}
