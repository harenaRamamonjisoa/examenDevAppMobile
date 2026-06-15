package com.tco.user.service.impl

import com.tco.dto.request.UpdateProfilRequest
import com.tco.user.model.Statut
import com.tco.user.model.User
import com.tco.user.repository.UserRepository
import com.tco.user.service.UserService

class UserServiceImpl(val userRepository: UserRepository)  : UserService {

    override suspend fun findUserForAdmin(userId: Long): User? {
        return userRepository.findById(userId)
    }

    override suspend fun updateStatusUser(statut: Statut) {
        TODO("Not yet implemented")
    }

    override suspend fun setProfile(
        userId: Long,
        request: UpdateProfilRequest
    ): User {
        val existingUser = userRepository.findById(userId) ?: throw NoSuchElementException("Profil introuvable")

        // On fusionne les modifications de manière isolée
        val updatedUser = existingUser.copy(
            firstName = request.firstName ?: existingUser.firstName,
            lastName = request.lastName ?: existingUser.lastName,
            email = request.email ?: existingUser.email
        )

        userRepository.update(updatedUser)
        return updatedUser
    }

    /**
     * Rôle 1 : L'administrateur clique sur le bouton (Autoriser, Suspendre, Refuser)
     */
    override suspend fun changeStatusUserForAdmin(userId: Long, statusString: String) {
        // 1. Récupérer l'utilisateur actuel
        val user = userRepository.findById(userId)
            ?: throw NoSuchElementException("Utilisateur introuvable")

        // 2. Convertir la chaîne en Enum de manière sécurisée
        val nouveauStatut = try {
            Statut.valueOf(statusString.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Statut invalide. Utilisez AUTHORIZED, SUSPENDED ou REFUSED.")
        }

        // 3. Modifier le statut et sauvegarder
        val userModifie = user.copy(statut = nouveauStatut)
        userRepository.update(userModifie)
    }

    override suspend fun findAllUsers(): List<User>? {
       return userRepository.findAll()
    }

    /**
     * Rôle 2 : L'application mobile interroge en boucle (Polling)
     */
    override suspend fun verifyStatusForMobile(userId: Long): Statut {
        val user = userRepository.findById(userId)

        // Cas A : L'utilisateur n'existe plus en base (déjà supprimé au cycle précédent)
        if (user == null) {
            return Statut.REFUSED
        }

        // Cas B : L'administrateur vient de refuser l'inscription (REFUSED)
        if (user.statut == Statut.REFUSED) {
            // L'application mobile va lire ce statut lors de cet appel HTTP.
            // On peut donc le supprimer immédiatement de la table pour nettoyer la base.
            userRepository.deleteById(userId)
            return Statut.REFUSED
        }

        // Cas C : Le statut est PENDING ou AUTHORIZED
        return user.statut
    }
}