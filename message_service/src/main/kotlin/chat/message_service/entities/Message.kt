package chat.message_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "messages")
data class Message(
    @Id
    var id: String? = null,
    var text: String,
    var senderId: String,
    var receiverId: String,
    var timestamp: Long = System.currentTimeMillis()
)