package chat.chat_service

import chat.chat_service.security.jwt.JwtUtil
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtUtilTest {

    private val secret = "test-secret"
    private val jwtUtil = JwtUtil(secret)

    @Test
    fun `validateToken should return subject if token is valid`() {
        val token = "valid-token"
        val claims = mockk<Claims> {
            every { subject } returns "user1"
        }

        mockkStatic(Jwts::class)
        every { Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body } returns claims

        val result = jwtUtil.validateToken(token)

        assertEquals("user1", result)
    }

    @Test
    fun `validateToken should return error message if token is invalid`() {
        val token = "invalid-token"

        mockkStatic(Jwts::class)
        every { Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body } throws Exception("Invalid token")

        val result = jwtUtil.validateToken(token)

        assertEquals("Can't validate token in JwtUtil chat_service", result)
    }
}