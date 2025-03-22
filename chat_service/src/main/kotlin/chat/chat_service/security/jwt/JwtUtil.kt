package chat.chat_service.security.jwt

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtUtil (
    @Value("\${jwt.secret}")
    private val secret: String
){
    fun validateToken(token: String): String?{
        return try{
            val body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
            body.subject
        } catch(e: Exception){
            "Can't validate token in JwtUtil chat_service"
        }
    }
}