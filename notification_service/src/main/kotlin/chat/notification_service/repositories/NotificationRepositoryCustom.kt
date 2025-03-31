package chat.notification_service.repositories

interface NotificationRepositoryCustom {
    fun markAsRead(senderId: String, receiverId: String)
}