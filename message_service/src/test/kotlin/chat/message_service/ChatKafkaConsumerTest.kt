package chat.message_service
import chat.message_service.entities.Message
import chat.message_service.kafka.ChatKafkaConsumer
import chat.message_service.repositories.MessageRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.springframework.data.redis.core.ValueOperations
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

class ChatKafkaConsumerTest {

    private val messageRepository: MessageRepository = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val redisTemplate: RedisTemplate<String, Any> = mockk(relaxed = true)
    private val consumer = ChatKafkaConsumer(messageRepository, objectMapper, redisTemplate)

    @Test
    fun `listen should process message and update cache when cache exists`() {
        val messageJson = """
            {
                "text": "Hello",
                "fileUrl": null,
                "senderId": "userA",
                "receiverId": "userB",
                "timestamp": 123456789
            }
        """.trimIndent()

        val cacheKey = consumer.getCacheKey("userA", "userB")
        val existingMessages = listOf(
            Message(id = "1", text = "Hi", senderId = "userA", receiverId = "userB", timestamp = 111L)
        )
        every { redisTemplate.opsForValue().get(cacheKey) } returns existingMessages

        every { messageRepository.save(any<Message>()) } returnsArgument 0

        consumer.listen(messageJson)

        verify {
            redisTemplate.opsForValue().set(
                cacheKey,
                match { (it as List<*>).size == existingMessages.size + 1 },
                Duration.ofMinutes(10)
            )
        }
    }

    @Test
    fun `listen should process message and not update cache when cache is null`() {
        val messageJson = """
        {
            "text": "Hello",
            "fileUrl": null,
            "senderId": "userA",
            "receiverId": "userB",
            "timestamp": 123456789
        }
    """.trimIndent()

        val cacheKey = consumer.getCacheKey("userA", "userB")

        val valueOperationsMock = mockk<ValueOperations<String, Any>>(relaxed = true)
        every { redisTemplate.opsForValue() } returns valueOperationsMock
        every { valueOperationsMock.get(cacheKey) } returns null

        every { messageRepository.save(any<Message>()) } returnsArgument 0

        consumer.listen(messageJson)

        verify(exactly = 0) { valueOperationsMock.set(any(), any(), any<Duration>()) }
    }


    @Test
    fun `listen should throw exception on invalid JSON`() {
        val invalidJson = "invalid json"
        val exception = assertThrows<Exception> {
            consumer.listen(invalidJson)
        }
        assertTrue(exception.message?.contains("Unrecognized token") == true ||
                exception.message?.contains("Unexpected character") == true)
    }
}