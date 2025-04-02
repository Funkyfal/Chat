package chat.message_service.kafka

import chat.message_service.entities.Message
import chat.message_service.repositories.MessageRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ChatKafkaConsumer(
    private val messageRepository: MessageRepository,
    private val objectMapper: ObjectMapper,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun getCacheKey(senderId: String, receiverId: String): String {
        return if (senderId < receiverId)
            "history:$senderId:$receiverId"
        else
            "history:$receiverId:$senderId"
    }

    @KafkaListener(topics = ["chat-messages"], groupId = "chat-group")
    fun listen(messageJson: String) {
        val node = objectMapper.readTree(messageJson)
        val msg = Message(
            text = if(node.has("text")) node["text"].asText() else null,
            fileUrl = if(node.has("fileUrl")) node["fileUrl"].asText() else null,
            senderId = node["senderId"].asText(),
            receiverId = node["receiverId"].asText(),
            timestamp = node["timestamp"].asLong()
        )
        messageRepository.save(msg)
        println("Сообщение сохранено в MongoDB: $msg")

        val key = getCacheKey(msg.senderId, msg.receiverId)
        val cached = redisTemplate.opsForValue().get(key)
        if (cached != null) {
            val messages: MutableList<Message> = objectMapper.convertValue<List<Message>>(
                cached,
                objectMapper.typeFactory.constructCollectionType(List::class.java, Message::class.java)
            ).toMutableList()
            messages.add(msg)
            redisTemplate.opsForValue().set(key, messages, Duration.ofMinutes(10))
            println("Обновили историю в кэше по ключу: $key")
        }
    }
}
