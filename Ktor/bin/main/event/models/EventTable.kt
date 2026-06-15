package com.tco.event.models

import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.core.Table

object EventTable : Table("event") {
    val idEvent = long("idEvent").autoIncrement()

    val nomEvent = varchar("nomEvent", 50)
    val dateEvent = date("dateEvent")
    val nomLieu = varchar("nom_lieu", 50)
    val longitudeLieu = float("longitude_lieu")
    val latitudeLieu = float("latitude_lieu")
    val capacite = integer("capacite")
    val description = text("description")
    val nombreParticipant = integer("nbre_participant")
}