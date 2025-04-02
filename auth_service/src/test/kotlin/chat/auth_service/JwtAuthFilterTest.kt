package chat.auth_service

import chat.auth_service.security.jwt.JwtAuthFilter
import chat.auth_service.security.jwt.JwtUtil
import chat.auth_service.security.services.CustomUserDetailsService
import io.mockk.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthFilterTest {

    private val jwtUtil = mockk<JwtUtil>()
    private val userDetailsService = mockk<CustomUserDetailsService>()
    private val filter = JwtAuthFilter(jwtUtil, userDetailsService)

    private val request = mockk<HttpServletRequest>(relaxed = true)
    private val response = mockk<HttpServletResponse>(relaxed = true)
    private val filterChain = mockk<FilterChain>(relaxed = true)

    @BeforeEach
    fun setup() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should pass request if Authorization header is missing`() {
        every { request.getHeader("Authorization") } returns null

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `should pass request if Authorization header does not start with Bearer`() {
        every { request.getHeader("Authorization") } returns "Basic token"

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `should pass request if token is invalid`() {
        every { request.getHeader("Authorization") } returns "Bearer invalidToken"
        every { jwtUtil.validateToken("invalidToken") } returns null

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `should authenticate user when token is valid`() {
        val token = "validToken"
        val username = "anton"
        val userDetails: UserDetails = User(username, "password", listOf(SimpleGrantedAuthority("ROLE_USER")))
        val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

        every { request.getHeader("Authorization") } returns "Bearer $token"
        every { jwtUtil.validateToken(token) } returns username
        every { userDetailsService.loadUserByUsername(username) } returns userDetails

        filter.doFilterInternal(request, response, filterChain)

        val securityContext = SecurityContextHolder.getContext()
        assertNotNull(securityContext.authentication)
        assertEquals(authToken.principal, securityContext.authentication.principal)

        verify { filterChain.doFilter(request, response) }
        verify { userDetailsService.loadUserByUsername(username) }
    }
}
