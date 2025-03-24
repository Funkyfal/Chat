package chat.message_service.kafka

import chat.message_service.entities.Message
import chat.message_service.repositories.MessageRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ChatKafkaConsumer(
    private val messageRepository: MessageRepository,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["chat-messages"], groupId = "chat-group")
    fun listen(messageJson: String){
        val node = objectMapper.readTree(messageJson)
        val msg = Message(
            text = node["text"].asText(),
            senderId = node["senderId"].asText(),
            receiverId = node["receiverId"].asText(),
            timestamp = node["timestamp"].asLong()
        )
        messageRepository.save(msg)
        println("Сообщение сохранено в MongoDB: $msg")
    }
}