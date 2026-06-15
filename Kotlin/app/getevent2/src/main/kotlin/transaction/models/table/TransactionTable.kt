package com.tco.transaction.models.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object TransactionTable : Table("transaction") {
    val idTransaction = long("id").autoIncrement()
    val montant = double("montant")
    val dateTransaction = timestamp("date_transaction")
    val modePaiement = varchar("mode_paiement", 50)
    val statutPaiement = varchar("statut_paiement", 25)
    val referencePaiement = long("reference_paiement")
    val idReservation = long("id_reservation")
}
