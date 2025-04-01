package chat.notification_service.services

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class NotificationCleanUpService(
    private val mongoTemplate: MongoTemplate
) {
    @Scheduled(cron = "0 0 * * * *")
    fun cleanUpReadNotifications() {
        val query = Query().addCriteria(Criteria.where("read").`is`(true))
        val deleted = mongoTemplate.remove(query, "notifications")
        println("Удалено уведомлений: ${deleted.deletedCount}")
    }
}