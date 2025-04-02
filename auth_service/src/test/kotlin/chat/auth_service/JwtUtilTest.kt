package chat.auth_service

import chat.auth_service.security.jwt.JwtUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class JwtUtilTest {

    private lateinit var jwtUtil: JwtUtil
    private val secretKey = Base64.getEncoder().encodeToString("superSecretKeyForTestsOfJavaUtil".toByteArray())

    @BeforeEach
    fun setup() {
        jwtUtil = JwtUtil(secretKey)
    }

    @Test
    fun `should generate a valid token`() {
        val token = jwtUtil.generateToken("anton")

        assertNotNull(token)
        assertTrue(token.isNotBlank())
    }

    @Test
    fun `should validate token and return username`() {
        val token = jwtUtil.generateToken("anton")
        val username = jwtUtil.validateToken(token)

        assertEquals("anton", username)
    }

    @Test
    fun `should return null for invalid token`() {
        val invalidToken = "invalid.token.string"

        val username = jwtUtil.validateToken(invalidToken)

        assertNull(username)
    }
}
