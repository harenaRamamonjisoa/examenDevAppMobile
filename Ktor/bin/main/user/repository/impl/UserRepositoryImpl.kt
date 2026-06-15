package com.tco.user.repository.impl

import com.tco.mapper.toUser
import com.tco.user.model.Role
import com.tco.user.model.Statut
import com.tco.user.model.User
import com.tco.user.model.table.UserTable
import com.tco.user.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class UserRepositoryImpl (private val database : R2dbcDatabase): UserRepository {

    override suspend fun findByEmail(email: String): User? {
        return suspendTransaction(database){
            UserTable
                .selectAll()
                .where { UserTable.email eq email}
                .map {row -> row.toUser()}
            .singleOrNull()
        }

    }

    // gère nativement le contexte asynchrone à travers les Flows.
    override suspend fun findById(id: Long): User? {
        return suspendTransaction(database) {
            UserTable
                .selectAll()
                // 2. Enveloppez l'id brut dans un EntityID pour que le 'eq' compile
                .where { UserTable.id eq EntityID(id, UserTable) }
                // 3. Transformez et mapz directement le résultat unique sans passer par .toList()
                .map { row -> row.toUser() }
                .singleOrNull()
        }
    }

    override suspend fun createUser(user: User): User {
        // 1. On enveloppe obligatoirement dans la transaction (si ce n'est pas fait plus haut)
        return suspendTransaction(database) {
            val resultRows = UserTable.insertReturning(listOf(UserTable.id)) {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[phoneNumber] = user.phoneNumber
            it[studentId] = user.studentId
            it[hashedPassword] = user.hashedPassword
            it[role] = user.role.toString()
            it[statut] = user.statut.toString()
        }
            // Extraction de la valeur brute du Long généré
            val generatedId = resultRows.first()[UserTable.id].value

            // Renvoi du modèle utilisateur final mis à jour
            user.copy(id = generatedId)

        }

    }

    /**
     * Rôle : Mettre à jour l'utilisateur complet (Profil, Statut, Rôle...)
     */
    override suspend fun update(user: User) {
        // On s'assure que l'ID n'est pas nul avant de tenter une mise à jour
        requireNotNull(user.id) { "Impossible de mettre à jour un utilisateur sans ID" }

        suspendTransaction(database) {
            // Enveloppez user.id dans un EntityID pour correspondre au type de la colonne
            UserTable.update({ UserTable.id eq EntityID(user.id, UserTable) }) {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[email] = user.email
                it[phoneNumber] = user.phoneNumber
                it[studentId] = user.studentId
                it[hashedPassword] = user.hashedPassword
                it[role] = user.role.toString()
                it[statut] = user.statut.toString()
            }
        }

    }

    override suspend fun deleteById(id: Long): Boolean {
        return suspendTransaction(database) {
            // Renvoie le nombre de lignes supprimées (0 ou 1)
            val rowsDeleted = UserTable.deleteWhere {
                UserTable.id eq EntityID(id, UserTable)
            }

            rowsDeleted > 0 // Renvoie true si supprimé, false si l'ID n'existait pas
        }
    }

    override suspend fun findAll(): List<User> {
        return suspendTransaction(database) {
            UserTable.selectAll()
                .map { row ->
                    User(
                        id = row[UserTable.id].value,
                        firstName = row[UserTable.firstName],
                        lastName = row[UserTable.lastName],
                        email = row[UserTable.email],
                        phoneNumber = row[UserTable.phoneNumber],
                        studentId = row[UserTable.studentId],
                        hashedPassword = row[UserTable.hashedPassword],
                        role = Role.valueOf((row[UserTable.role])),
                        statut = Statut.valueOf(row[UserTable.statut])
                    )
                }.toList()
        }

    }
}