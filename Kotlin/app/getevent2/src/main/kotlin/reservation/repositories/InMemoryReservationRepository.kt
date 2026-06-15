package reservation.repositories

import com.tco.event.models.EventTable
import com.tco.mapper.toReservation
import com.tco.reservation.models.table.ReservationTable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import reservation.models.Reservation


class InMemoryReservationRepository(private val database: R2dbcDatabase) : ReservationRepository {

    private val reservations = mutableListOf<Reservation>()
//    private var nextId: Long = 1
//
//    init {
//        reservations.add(
//            Reservation(
//                idReservation = nextId++,
//                dateReservation = Date(),
//                idEvent = 1,
//                statut = StatutReservation.PAYANT
//            )
//        )
//        reservations.add(
//            Reservation(
//                idReservation = nextId++,
//                dateReservation = Date(),
//                idEvent = 2,
//                statut = StatutReservation.NON_PAYANT
//            )
//        )
//    }

    override suspend fun findAll(): List<Reservation> {

        return suspendTransaction(database){
            ReservationTable
                .selectAll()
                .map { row -> row.toReservation() }
                .toList()
        }
    }

    override suspend fun findById(idReservation: Long): Reservation? {
        return suspendTransaction(database){
            ReservationTable
                .selectAll()
                .where { ReservationTable.idReservation eq idReservation }
                .map { row -> row.toReservation() }
                .singleOrNull()
        }
    }

    override suspend fun save(reservation: Reservation): Reservation {
        return suspendTransaction(database) {
            val resultRows = ReservationTable.insertReturning(listOf(ReservationTable.idReservation)) {
                it[dateReservation] = reservation.dateReservation
                it[idEvent] = reservation.idEvent
                it[idUser] = reservation.idUser
                it[statut] = reservation.statut.toString()
            }
            val generatedId = resultRows.first()[ReservationTable.idReservation]
            reservation.copy(idReservation = generatedId)
        }
    }

    override suspend fun update(idReservation: Long, reservation: Reservation): Reservation? {
        return suspendTransaction(database) {
            val affectedRows = ReservationTable.update({ ReservationTable.idReservation eq idReservation })
            { row ->
                row[statut] = reservation.statut.toString()
                row[idEvent] = reservation.idEvent
                row[idUser] = reservation.idUser
                row[dateReservation] = reservation.dateReservation
            }

            // Si PostgreSQL confirme la modification (> 0), on retourne l'objet enrichi de son ID
            if (affectedRows > 0) reservation.copy(idReservation = idReservation) else null
        }
    }

    // 2. ÉQUIVALENCE DELETE (Supprime l'élément et retourne un booléen de confirmation)
    override suspend fun delete(idReservation: Long): Boolean {
        return suspendTransaction(database) {
            val affectedRows = ReservationTable.deleteWhere { ReservationTable.idReservation eq idReservation }

            affectedRows > 0 // Renvoie true si la ligne existait et a été supprimée, false sinon
        }
    }

    // 3. ÉQUIVALENCE FINDBYEVENT (Filtre et retourne une liste)
    override suspend fun findByEvent(idEvent: Long): List<Reservation> {
        return suspendTransaction(database) {
            ReservationTable
                .selectAll()
                .where { ReservationTable.idEvent eq idEvent }
                .map { row -> row.toReservation() }
                .toList()
        }
    }

    override suspend fun findByUserId(idUser: Long): List<Reservation> {
        return suspendTransaction(database) {
            ReservationTable
                .selectAll()
                .where { ReservationTable.idUser eq idUser }
                .map { row -> row.toReservation() }
                .toList()
        }
    }
}