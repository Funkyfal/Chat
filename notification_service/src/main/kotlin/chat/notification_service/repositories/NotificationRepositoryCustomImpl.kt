package chat.notification_service.repositories

import chat.notification_service.entities.Notification
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

class NotificationRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
): NotificationRepositoryCustom{

    override fun markAsRead(senderId: String, receiverId: String) {
        val query = Query().apply {
            addCriteria(Criteria.where("senderId").`is`(senderId))
            addCriteria(Criteria.where("receiverId").`is`(receiverId))
            addCriteria(Criteria.where("read").`is`(false))
        }
        val update = Update().set("read", true)
        mongoTemplate.updateMulti(query, update, Notification::class.java)
    }
}