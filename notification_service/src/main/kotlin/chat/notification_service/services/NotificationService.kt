package chat.notification_service.services

import chat.notification_service.dto.NotificationRequest
import chat.notification_service.entities.Notification
import chat.notification_service.repositories.NotificationRepository
import org.springframework.stereotype.Service

@Service
class NotificationService (
    private val notificationRepository: NotificationRepository
){
    fun addNotificationToDB(notification: Notification): Notification{
        return notificationRepository.save(notification)
    }

    fun markNotificationAsRead(request: NotificationRequest){
        notificationRepository.markAsRead(request.senderId, request.receiverId)
    }

    fun findAllNotifications(receiverId: String): List<Notification>{
        return notificationRepository.findByReceiverIdAndReadFalse(receiverId)
    }
}