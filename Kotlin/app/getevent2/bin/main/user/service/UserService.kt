package com.tco.user.service

import com.tco.dto.request.UpdateProfilRequest
import com.tco.user.model.Statut
import com.tco.user.model.User

interface UserService {

    suspend fun findUserForAdmin(userId: Long): User?

    suspend fun updateStatusUser(statut : Statut)

    suspend fun setProfile(userId :Long, request: UpdateProfilRequest) : User

    suspend fun changeStatusUserForAdmin(userId: Long, statusString : String)

    suspend fun findAllUsers(): List<User> ?

    suspend fun verifyStatusForMobile(userId: Long): Statut
}