package chat.auth_service

import chat.auth_service.entities.User
import chat.auth_service.repositories.UserRepository
import chat.auth_service.security.services.CustomUserDetailsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.userdetails.UsernameNotFoundException

class CustomUserDetailsServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val userDetailsService = CustomUserDetailsService(userRepository)

    @Test
    fun `should load user by username successfully`() {
        val user = User(id = 1, username = "anton", password = "hashedPassword", roles = setOf("ROLE_USER"))

        every { userRepository.findByUsername("anton") } returns user

        val userDetails = userDetailsService.loadUserByUsername("anton")

        assertNotNull(userDetails)
        assertEquals("anton", userDetails.username)
        assertEquals("hashedPassword", userDetails.password)
        assertTrue(userDetails.authorities.any { it.authority == "ROLE_USER" })

        verify { userRepository.findByUsername("anton") }
    }

    @Test
    fun `should throw exception when user not found`() {
        every { userRepository.findByUsername("anton") } returns null

        val exception = assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername("anton")
        }

        assertEquals("User not found", exception.message)
        verify { userRepository.findByUsername("anton") }
    }
}
