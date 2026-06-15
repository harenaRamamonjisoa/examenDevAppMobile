package com.tco.mapper

import com.tco.user.model.Role
import com.tco.user.model.Statut
import com.tco.user.model.User
import com.tco.user.model.table.UserTable
import org.jetbrains.exposed.v1.core.ResultRow


fun ResultRow.toUser() : com.tco.user.model.User {

    return _root_ide_package_.com.tco.user.model.User(
        id = this[_root_ide_package_.com.tco.user.model.table.UserTable.id].value,
        firstName = this[_root_ide_package_.com.tco.user.model.table.UserTable.firstName],
        lastName = this[_root_ide_package_.com.tco.user.model.table.UserTable.lastName],
        email = this[_root_ide_package_.com.tco.user.model.table.UserTable.email],
        phoneNumber = this[_root_ide_package_.com.tco.user.model.table.UserTable.phoneNumber],
        studentId = this[_root_ide_package_.com.tco.user.model.table.UserTable.studentId],
        hashedPassword = this[_root_ide_package_.com.tco.user.model.table.UserTable.hashedPassword],
        role = _root_ide_package_.com.tco.user.model.Role.valueOf(this[_root_ide_package_.com.tco.user.model.table.UserTable.role]),
        statut = _root_ide_package_.com.tco.user.model.Statut.valueOf(this[_root_ide_package_.com.tco.user.model.table.UserTable.statut])

    )
}