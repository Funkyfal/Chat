package chat.notification_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "notifications")
data class Notification (
    @Id
    val id: String? = null,
    val type: String,
    val senderId: String,
    val receiverId: String,
    val messagePreview: String,
    val timestamp: Instant = Instant.now(),
    val read: Boolean
)