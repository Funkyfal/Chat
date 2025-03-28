package chat.message_service.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.secret}")
    private val secret: String) {

    fun generateToken(username: String): String{
        val claims = Jwts.claims().setSubject(username)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    fun validateToken(token: String): String?{
        return try{
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body.subject
        } catch (e: Exception){
            null
        }
    }
}