package com.tco.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtAudience = "jwt-audience"
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtRealm = "ktor sample app"
    val jwtSecret = "secret"

    authentication {
        // AJUSTEMENT 1 : On donne le nom "auth-jwt" à la configuration
        // pour qu'elle corresponde exactement à votre `authenticate("auth-jwt")`
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                // AJUSTEMENT 2 : En plus de l'audience, on valide que le token
                // contient bien la clé "userId" pour éviter des jetons anonymes.
                val hasUserId = credential.payload.getClaim("userId").asString() != null

                if (credential.payload.audience.contains(jwtAudience) && hasUserId) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
