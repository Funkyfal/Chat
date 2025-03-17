package chat.message_service.kafka

import chat.message_service.Message
import chat.message_service.MessageRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ChatKafkaConsumer(private val messageRepository: MessageRepository) {

    @KafkaListener(topics = ["chat-messages"], groupId = "chat-group")
    fun listen(message: String){
        println("Получено сообщение из Kafka: $message")
        val savedMessage = messageRepository.save(Message(text = message))
        println("Сообщение сохранено в MongoDB: $savedMessage")
    }
}