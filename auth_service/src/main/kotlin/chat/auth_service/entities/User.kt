package chat.auth_service.entities

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(unique = true)
    val username: String,

    val password: String,

    @ElementCollection(fetch = FetchType.EAGER)
    val roles: Set<String>
)