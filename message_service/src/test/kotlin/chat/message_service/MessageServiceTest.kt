package chat.message_service

import chat.message_service.entities.Message
import chat.message_service.repositories.MessageRepository
import chat.message_service.services.MessageService
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class MessageServiceTest {

    private val messageRepository: MessageRepository = mockk()
    private val redisTemplate: RedisTemplate<String, Any> = mockk(relaxed = true)
    private val messageService = MessageService(messageRepository, redisTemplate)

    @Test
    fun `getHistory should return cached history if present`() {
        val userA = "userA"
        val userB = "userB"
        val key = "history:$userA:$userB"
        val cachedList = listOf(
            Message(id = "1", text = "Cached message", senderId = userA, receiverId = userB, timestamp = 111L)
        )
        every { redisTemplate.opsForValue().get(key) } returns cachedList

        val result = messageService.getHistory(userA, userB)

        assertEquals(1, result.size)
        assertEquals("Cached message", result[0].text)
    }

    @Test
    fun `getHistory should return history from MongoDB if cache is empty and cache it`() {
        val userA = "userA"
        val userB = "userB"
        val key = "history:$userA:$userB"

        val valueOps = mockk<ValueOperations<String, Any>>()
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get(key) } returns null

        val mongoHistory = listOf(
            Message(id = "2", text = "MongoDB message", senderId = userA, receiverId = userB, timestamp = 222L)
        )
        every { messageRepository.findAllByParticipants(userB, userA) } returns mongoHistory
        every { valueOps.set(key, any(), any<Duration>()) } just Runs

        val result = messageService.getHistory(userA, userB)

        assertEquals(1, result.size)
        assertEquals("MongoDB message", result[0].text)

        verify(exactly = 1) { valueOps.set(key, any(), any<Duration>()) }
    }


}