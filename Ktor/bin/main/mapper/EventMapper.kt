package com.tco.mapper

import com.tco.event.models.EventTable
import event.models.Event
import event.models.Lieu
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toEvent() : Event {
        return Event(
            // .value permet de convertir l'EntityID<Long> de la table en Long brut
            idEvent = this[EventTable.idEvent],
            nomEvent = this[EventTable.nomEvent],
            dateEvent = this[EventTable.dateEvent],
            description = this[EventTable.description],
            lieu = Lieu(
                nom = this[EventTable.nomLieu],
                longitude = this[EventTable.longitudeLieu],
                latitude = this[EventTable.latitudeLieu],
                capacite = this[EventTable.capacite]
            )
        )
}