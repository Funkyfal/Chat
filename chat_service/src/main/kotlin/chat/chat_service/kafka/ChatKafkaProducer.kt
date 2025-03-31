package chat.chat_service.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ChatKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
){
    fun sendMessage(message: String){
        println("Отправка сообщения: $message")
        kafkaTemplate.send("chat-messages", message)
    }

    fun sendNotification(notification: String){
        println("Отправка уведомления $notification")
        kafkaTemplate.send("notification-topic", notification)
    }
}