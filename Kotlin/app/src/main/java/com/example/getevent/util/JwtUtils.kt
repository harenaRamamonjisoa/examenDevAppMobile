package com.example.getevent.util

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun getRoleFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val jsonObject = JSONObject(payload)
            jsonObject.optString("role", "user")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
