package chat.notification_service.dto

data class NotificationRequest(
    val senderId: String,
    val receiverId: String
)