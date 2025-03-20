package chat.auth_service.repositories

import chat.auth_service.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long>{
    fun findByUsername(username: String): User?
}