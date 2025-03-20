package chat.auth_service.security.dto

data class RegisterRequest(
    val username: String, val password: String, val roles: Set<String> = setOf("ROLE_USER")
)

data class LoginRequest(val username: String, val password: String)
data class JwtResponse(val token: String)
