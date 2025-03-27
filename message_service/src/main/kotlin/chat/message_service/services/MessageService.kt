package chat.message_service.services

import chat.message_service.entities.Message
import chat.message_service.repositories.MessageRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class MessageService (
    private val messageRepository: MessageRepository,
    private val redisTemplate: RedisTemplate<String, Any>
){
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun getHistory(userA: String, userB: String): List<Message>{
        val key = "history:$userA:$userB"
        val cached = redisTemplate.opsForValue().get(key)

        if (cached is List<*>) {
            println("Вернули историю из кэша")
            return objectMapper.convertValue(cached, objectMapper.typeFactory.constructCollectionType(List::class.java, Message::class.java))
        }

        println("Вернули историю из MongoDB и закинули в кэш")
        val messages = messageRepository.findAllByParticipants(userB, userA)
        redisTemplate.opsForValue().set(key, messages, Duration.ofMinutes(10))
        return messages
    }
}
