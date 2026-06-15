package com.tco.mapper

import com.tco.transaction.models.table.TransactionTable
import org.jetbrains.exposed.v1.core.ResultRow
import transaction.models.StatutPaiement
import transaction.models.Transaction

fun ResultRow.toTransaction(): Transaction {
    return Transaction(
        idTransaction = this[TransactionTable.idTransaction],
        montant = this[TransactionTable.montant],
        dateTransaction = this[TransactionTable.dateTransaction],
        modePaiement = this[TransactionTable.modePaiement],
        statutPaiement = StatutPaiement.valueOf(this[TransactionTable.statutPaiement]),
        referencePaiement = this[TransactionTable.referencePaiement],
        idReservation = this[TransactionTable.idReservation]
    )
}
