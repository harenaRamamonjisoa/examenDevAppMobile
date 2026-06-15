package com.tco.security.passwordHasher.impl

import com.tco.security.passwordHasher.PasswordHasher
import org.mindrot.jbcrypt.BCrypt

class BCryptPasswordHasher : PasswordHasher {
    override fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    override fun verify(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}