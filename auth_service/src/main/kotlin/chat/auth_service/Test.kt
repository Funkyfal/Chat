package chat.auth_service

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Test(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long,

    val username:String,

    val password:String,

    val role:String
)