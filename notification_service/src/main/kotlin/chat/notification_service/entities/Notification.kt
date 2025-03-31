package chat.notification_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "notifications")
data class Notification (
    @Id
    val id: String? = null,
    //заглушка с потенциалом на будущие уведомления о добавлении в группу и т.д.
    val type: String = "MESSAGE",
    val senderId: String,
    val receiverId: String,
    val messagePreview: String,
    val timestamp: Long,
    val read: Boolean
)