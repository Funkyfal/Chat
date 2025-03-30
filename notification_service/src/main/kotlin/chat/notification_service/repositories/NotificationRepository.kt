package chat.notification_service.repositories

import chat.notification_service.entities.Notification
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query


interface NotificationRepository: MongoRepository<Notification, String> {
    @Query("{ 'senderId': ?0, 'receiverId': ?1, 'read': false }")
    fun markAsRead(senderId: String, receiverId: String)

    fun findByReceiverIdAndReadFalse(receiverId: String): List<Notification>
}