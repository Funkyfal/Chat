//package chat.chat_service.security.jwt
//
//import jakarta.servlet.FilterChain
//import org.springframework.stereotype.Component
//import org.springframework.web.filter.OncePerRequestFilter
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.context.SecurityContextHolder
//
//@Component
//class JwtAuthFilter(
//    private val jwtUtil: JwtUtil
//) : OncePerRequestFilter() {
//    override fun doFilterInternal(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        filterChain: FilterChain
//    ){
//        val authHeader = request.getHeader("Authorization")
//        if (authHeader.isNullOrEmpty() || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response)
//            return
//        }
//
//        val token = authHeader.substring(7)
//        val username = jwtUtil.validateToken(token)
//
//        if (username != null) {
//            val authToken = UsernamePasswordAuthenticationToken(username, null, emptyList())
//            SecurityContextHolder.getContext().authentication = authToken
//        }
//
//        filterChain.doFilter(request, response)
//    }
//}