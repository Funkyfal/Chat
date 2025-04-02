package chat.message_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "messages")
data class Message(
    @Id
    val id: String? = null,
    val text: String? = null,
    val fileUrl: String? = null,
    val senderId: String,
    val receiverId: String,
    val timestamp: Long = System.currentTimeMillis()
)