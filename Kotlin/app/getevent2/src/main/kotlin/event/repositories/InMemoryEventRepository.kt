package event.repositories

import com.tco.event.models.EventTable
import com.tco.mapper.toEvent
import event.models.Event
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

class InMemoryEventRepository(private val database: R2dbcDatabase) : EventRepository {

    private val events = mutableListOf<Event>()
//    private var nextId: Long = 1
//
//    init {
//        // Données de test
//        events.add(
//            Event(
//                idEvent = nextId++,
//                nomEvent = "Conférence Intelligence Artificielle",
//                dateEvent = Date(),
//                lieu = Lieu(
//                    nom = "Amphithéâtre A",
//                    longitude = 2.3522f,
//                    latitude = 48.8566f,
//                    capacite = 200
//                ),
//                description = "Découvrez les dernières avancées en IA avec des experts du domaine",
//                nombreParticipants = 0
//            )
//        )
//        events.add(
//            Event(
//                idEvent = nextId++,
//                nomEvent = "Workshop Kotlin & Ktor",
//                dateEvent = LocalDate(),
//                lieu = Lieu(
//                    nom = "Salle B12",
//                    longitude = 2.3488f,
//                    latitude = 48.8534f,
//                    capacite = 30
//                ),
//                description = "Apprenez à créer des APIs REST avec Kotlin et Ktor",
//                nombreParticipants = 0
//            )
//        )
//        events.add(
//            Event(
//                idEvent = nextId++,
//                nomEvent = "Soirée d'Intégration",
//                dateEvent = Date(),
//                lieu = Lieu(
//                    nom = "Hall Principal",
//                    longitude = 2.3500f,
//                    latitude = 48.8550f,
//                    capacite = 150
//                ),
//                description = "Rencontrez les autres étudiants autour d'un buffet",
//                nombreParticipants = 0
//            )
//        )
//    }

    // CREATE
    override suspend fun save(event: Event): Event {
       // val newEvent = event.copy(idEvent = nextId++)
        events.add(event)
        suspendTransaction(database) {
            EventTable.insert{
                it[nomEvent] = event.nomEvent
                it[dateEvent] = event.dateEvent
                it[nomLieu] = event.lieu.nom
                it[latitudeLieu] = event.lieu.latitude
                it[longitudeLieu] = event.lieu.longitude
                it[capacite] = event.lieu.capacite
                it[nombreParticipant] = event.nombreParticipants
                it[description] = event.description
                it[estPrive] = event.estPrive
            }

        }
        return event
    }

    // READ ALL : Récupère tous les événements
    override suspend fun findAll(): List<Event> {
        return suspendTransaction(database) {
            EventTable
                .selectAll()
                .map { row -> row.toEvent() }
                .toList()// Utilise votre extension de mapping existante
        }
    }

    // READ ONE : Récupère un événement par son identifiant unique
    override suspend fun findById(idEvent: Long): Event? {
        return suspendTransaction(database) {
            EventTable
                .selectAll()
                .where { EventTable.idEvent eq idEvent }
                .map { row -> row.toEvent() }
                .singleOrNull()
        }
    }

    // UPDATE : Remplace l'intégralité d'un événement
    override suspend fun update(idEvent: Long, event: Event): Event? {
        return suspendTransaction(database) {
            // Exécute la mise à jour globale dans PostgreSQL
            val affectedRows = EventTable.update({ EventTable.idEvent eq idEvent }) { row ->
                row[nomEvent] = event.nomEvent
                row[dateEvent] = event.dateEvent
                row[nomLieu] = event.lieu.nom
                row[longitudeLieu] = event.lieu.longitude
                row[latitudeLieu] = event.lieu.latitude
                row[capacite] = event.lieu.capacite
                row[nombreParticipant] = event.nombreParticipants
                row[description] = event.description
                row[estPrive] = event.estPrive
            }

            // Si une ligne a été modifiée, on retourne l'objet à jour, sinon null
            if (affectedRows > 0) event.copy(idEvent = idEvent) else null
        }
    }

    // DELETE : Supprime un événement et indique si l'action a réussi
    override suspend fun delete(idEvent: Long): Boolean {
        return suspendTransaction(database) {
            val affectedRows = EventTable.deleteWhere { EventTable.idEvent eq idEvent }
            affectedRows > 0 // Renvoie true si un élément a été supprimé, false sinon
        }
    }
}