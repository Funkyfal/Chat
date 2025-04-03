package chat.notification_service

import chat.notification_service.security.jwt.JwtUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtUtilTest {

    private val secret = "myTestSecretForTestItsSoLongItNeedsToBeSuperLong123"
    private val jwtUtil = JwtUtil(secret)

    @Test
    fun `generateToken and validateToken must work correct`() {
        val username = "testUser"
        val token = jwtUtil.generateToken(username)

        val subject = jwtUtil.validateToken(token)
        assertEquals(username, subject)
    }
}