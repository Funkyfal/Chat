package chat.auth_service.security.controllers

import chat.auth_service.entities.User
import chat.auth_service.repositories.UserRepository
import chat.auth_service.security.dto.JwtResponse
import chat.auth_service.security.dto.LoginRequest
import chat.auth_service.security.dto.RegisterRequest
import chat.auth_service.security.jwt.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<String> {
        if (userRepository.findByUsername(request.username) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists")
        }
        val newUser = User(
            id = null,
            username = request.username,
            password = passwordEncoder.encode(request.password),
            roles = setOf("ROLE_USER")
        )
        userRepository.save(newUser)
        return ResponseEntity.ok("User registered successfully")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<JwtResponse> {
        val user = userRepository.findByUsername(request.username)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        // Проверяем пароль
        if (!passwordEncoder.matches(request.password, user.password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        // Генерируем токен
        val token = jwtUtil.generateToken(user.username)
        return ResponseEntity.ok(JwtResponse(token))
    }
}