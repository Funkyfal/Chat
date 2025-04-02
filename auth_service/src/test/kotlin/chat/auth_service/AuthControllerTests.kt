package chat.auth_service

import chat.auth_service.entities.User
import chat.auth_service.repositories.UserRepository
import chat.auth_service.security.controllers.AuthController
import chat.auth_service.security.dto.LoginRequest
import chat.auth_service.security.dto.RegisterRequest
import chat.auth_service.security.jwt.JwtUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder

class AuthControllerTests {

    private lateinit var authController: AuthController
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val jwtUtil: JwtUtil = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        authController = AuthController(userRepository, passwordEncoder, jwtUtil)
    }

    @Test
    fun `register should create a new user when username is unique`() {
        val request = RegisterRequest("anton", "password123", setOf("ROLE_USER"))

        every { userRepository.findByUsername("anton") } returns null
        every { passwordEncoder.encode("password123") } returns "hashedPassword"
        every { userRepository.save(any()) } returns User(1L, "anton", "hashedPassword", setOf("ROLE_USER"))

        val response: ResponseEntity<String> = authController.register(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("User registered successfully", response.body)

        verify { userRepository.findByUsername("anton") }
        verify { passwordEncoder.encode("password123") }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `register should return BAD_REQUEST when user already exists`() {
        val request = RegisterRequest("anton", "password123", setOf("ROLE_USER"))
        every { userRepository.findByUsername("anton") } returns User(
            1L, "anton", "hashedPassword", setOf("ROLE_USER")
        )

        val response: ResponseEntity<String> = authController.register(request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("User already exists", response.body)

        verify { userRepository.findByUsername("anton") }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `login should return JWT token when credentials are correct`() {
        val request = LoginRequest("anton", "password123")
        val user = User(1L, "anton", "hashedPassword", setOf("ROLE_USER"))

        every { userRepository.findByUsername("anton") } returns user
        every { passwordEncoder.matches("password123", "hashedPassword") } returns true
        every { jwtUtil.generateToken("anton") } returns "valid.jwt.token"

        val response: ResponseEntity<*> = authController.login(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("valid.jwt.token", (response.body as? chat.auth_service.security.dto.JwtResponse)?.token)

        verify { userRepository.findByUsername("anton") }
        verify { passwordEncoder.matches("password123", "hashedPassword") }
        verify { jwtUtil.generateToken("anton") }
    }

    @Test
    fun `login should return UNAUTHORIZED when user is not found`() {
        val request = LoginRequest("nonexistent", "password123")
        every { userRepository.findByUsername("nonexistent") } returns null

        val response: ResponseEntity<*> = authController.login(request)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertNull(response.body)

        verify { userRepository.findByUsername("nonexistent") }
    }

    @Test
    fun `login should return UNAUTHORIZED when password is incorrect`() {
        val request = LoginRequest("anton", "wrongpassword")
        val user = User(1L, "anton", "hashedPassword", setOf("ROLE_USER"))

        every { userRepository.findByUsername("anton") } returns user
        every { passwordEncoder.matches("wrongpassword", "hashedPassword") } returns false

        val response: ResponseEntity<*> = authController.login(request)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertNull(response.body)

        verify { userRepository.findByUsername("anton") }
        verify { passwordEncoder.matches("wrongpassword", "hashedPassword") }
    }
}
