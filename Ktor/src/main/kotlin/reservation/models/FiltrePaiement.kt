package reservation.models

enum class FiltrePaiement {
    TOUS,
    NON_PAYE,
    EN_ATTENTE,
    PAYE;

    companion object {
        fun fromQuery(value: String?): FiltrePaiement? {
            if (value.isNullOrBlank()) return TOUS
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
