package chat.notification_service.dto

data class NotificationRequest(
    val receiverId: String,
    val senderId: String
)