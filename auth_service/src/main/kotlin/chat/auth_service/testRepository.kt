package chat.auth_service

import org.springframework.data.jpa.repository.JpaRepository

interface testRepository: JpaRepository<Test, Long> {
}