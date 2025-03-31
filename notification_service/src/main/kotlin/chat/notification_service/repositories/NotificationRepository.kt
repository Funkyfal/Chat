package chat.notification_service.repositories

import chat.notification_service.entities.Notification
import org.springframework.data.mongodb.repository.MongoRepository


interface NotificationRepository: MongoRepository<Notification, String>, NotificationRepositoryCustom {
    fun findByReceiverIdAndReadFalse(receiverId: String): List<Notification>
}