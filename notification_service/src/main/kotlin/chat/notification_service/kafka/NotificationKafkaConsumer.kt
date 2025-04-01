package chat.notification_service.kafka

import chat.notification_service.entities.Notification
import chat.notification_service.repositories.NotificationRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NotificationKafkaConsumer(
    private val notificationRepository: NotificationRepository,
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
    ) {

    @KafkaListener(topics = ["notification-topic"], groupId = "notification_group")
    fun listen(record: ConsumerRecord<String, String>){
        val notification = objectMapper.readValue<Notification>(record.value())

        val activeChat = redisTemplate.opsForValue().get("active_chat:${notification.receiverId}")

        if(activeChat == null || activeChat != notification.senderId){
            redisTemplate.convertAndSend("notification:${notification.receiverId}", record.value())
            notificationRepository.save(notification)
        }
    }
}