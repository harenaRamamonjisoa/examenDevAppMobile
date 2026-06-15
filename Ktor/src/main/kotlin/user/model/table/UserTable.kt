package com.tco.user.model.table


import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object UserTable : IdTable<Long>("user"){

    override val id: Column<EntityID<Long>> = long("id").autoIncrement().entityId()

    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 255)
    val phoneNumber = varchar("phone_number", 50)
    val studentId = varchar("student_id", 50)
    val role = varchar("role", 50)
    val statut = varchar("statut", 50)
    val hashedPassword = varchar("password", 255)

}